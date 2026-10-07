#!/usr/bin/env python3
"""관광지 상세 조회(GET /api/locations/{id}) 지연 측정 - TourAPI 캐시 적용 전/후 동일 조건 비교용.

- 대상 (flow-data/ CSV 기준, export-flow-data.sh로 생성)
    tourapi : locations.csv 중 curated_locations.csv에 없는 관광지 (상세 조회 시 TourAPI 실시간 호출)
    curated : curated_locations.csv (TourAPI 호출 없음 - 네트워크 왕복 기준선 대조군)
  정렬된 목록에서 고정 간격으로 N곳을 뽑으므로, 같은 CSV면 매 실행 같은 ID가 선택된다.
- 라운드 R회 반복 (라운드마다 같은 ID를 고정 시드로 섞은 순서로 조회) -> 1라운드 cold, 이후 warm 비교 가능
- 동시성 C (스레드별 keep-alive 연결)
- tourapi 대상은 요청 1건당 TourAPI 최대 2건 호출 -> N*R*2가 개발계정 1일 한도(1,000건)를 넘지 않게 막는다.

사용 예:
  python bench-location-detail.py baseline-tourapi                       # tourapi, N=50 R=4 C=5
  python bench-location-detail.py baseline-curated --target curated
  python bench-location-detail.py after-tourapi --host 13.210.218.196 --port 8080

결과: results/tourapi-cache/<label>-<시각>/{raw.csv, summary.txt}

캐시 미적용 vs 적용 비교 (Grafana 구간을 길게 만들기 위해 rps 고정, TRACEK_SSH_KEY 필요):
  export TRACEK_SSH_KEY=~/Downloads/KRoute.pem
  python bench-location-detail.py nocache-tourapi --no-cache    --rps 1   # 라운드마다 캐시 삭제 -> 매 요청 TourAPI 호출
  (2분 이상 쉬기 - Grafana에서 두 구간이 섞이지 않도록)
  python bench-location-detail.py cache-tourapi   --flush-before --rps 1  # 1라운드 miss -> 2~4라운드 hit
  50곳 x 4라운드 x rps 1 = 약 3.5분씩, TourAPI 사용량 약 400 + 100 = 500건
"""
import argparse
import csv
import http.client
import json
import os
import random
import statistics
import subprocess
import threading
import time
from datetime import datetime

HERE = os.path.dirname(os.path.abspath(__file__))
FLOW_DATA = os.path.join(HERE, "flow-data")
OUT_ROOT = os.path.join(HERE, "results", "tourapi-cache")
TOURAPI_DAILY_GUARD = 900


def read_ids(name):
    with open(os.path.join(FLOW_DATA, name), encoding="utf-8") as f:
        return [int(line.split(",")[0]) for line in f.read().split() if line.strip()]


def pick_ids(target, n):
    curated = read_ids("curated_locations.csv")
    if target == "curated":
        ids = curated
    else:
        excluded = set(curated)
        ids = sorted(i for i in read_ids("locations.csv") if i not in excluded)
    step = max(1, len(ids) // n)
    return ids[::step][:n]


def pct(values, p):
    s = sorted(values)
    k = max(0, min(len(s) - 1, int(round(p / 100 * len(s) + 0.5)) - 1))
    return s[k]


def flush_tourapi_cache(args):
    """운영 Redis의 tourapi:* 캐시 키만 삭제한다 (다른 키는 건드리지 않음). SSH 키는 TRACEK_SSH_KEY 환경변수로 받는다."""
    key_path = os.environ.get("TRACEK_SSH_KEY")
    assert key_path, "--no-cache / --flush-before 는 TRACEK_SSH_KEY(백엔드 EC2 SSH 키 경로) 환경변수가 필요합니다"
    remote = (
        'P=$(grep ^REDIS_PASSWORD= ~/backend/.env | cut -d= -f2-); '
        'R="docker exec tracek-redis redis-cli -a $P --no-auth-warning"; '
        '$R --scan --pattern "tourapi:*" | xargs -r -n 100 $R DEL > /dev/null; '
        'echo remaining=$($R --scan --pattern "tourapi:*" | wc -l)'
    )
    out = subprocess.run(
        ["ssh", "-i", key_path, "-o", "BatchMode=yes", f"{args.ssh_user}@{args.host}", remote],
        capture_output=True, text=True, timeout=60,
    )
    print(f"  [cache flush] {out.stdout.strip() or out.stderr.strip()}")
    assert "remaining=0" in out.stdout, "캐시 삭제 실패 - 측정을 중단합니다"


def run(args):
    if args.target == "tourapi":
        # no-cache면 모든 요청이 TourAPI 2회, 캐시 적용이면 1라운드만 호출
        api_rounds = args.rounds if args.no_cache else 1
        assert args.n * api_rounds * 2 <= TOURAPI_DAILY_GUARD, "TourAPI 1일 한도 초과 위험 - N/R을 줄이세요"

    ids = pick_ids(args.target, args.n)
    rng = random.Random(42)
    rounds = []
    for r in range(args.rounds):
        order = ids[:]
        rng.shuffle(order)
        rounds.append([(r + 1, i) for i in order])

    if args.flush_before and not args.no_cache:
        flush_tourapi_cache(args)

    results, lock, cursor = [], threading.Lock(), [0]
    interval = 1.0 / args.rps if args.rps else 0.0
    slot = [0.0]  # 다음 요청 발사 시각 (rps 고정용)

    def worker(plan):
        conn = http.client.HTTPConnection(args.host, args.port, timeout=15)
        while True:
            with lock:
                if cursor[0] >= len(plan):
                    break
                rnd, loc = plan[cursor[0]]
                cursor[0] += 1
                if interval:
                    fire_at = max(slot[0], time.perf_counter())
                    slot[0] = fire_at + interval
                else:
                    fire_at = 0.0
            if interval:
                time.sleep(max(0.0, fire_at - time.perf_counter()))
            t0 = time.perf_counter()
            try:
                conn.request("GET", f"/api/locations/{loc}")
                resp = conn.getresponse()
                body = resp.read()
                status = resp.status
                images = len(json.loads(body)["data"].get("images") or []) if status == 200 else -1
            except Exception:
                conn.close()
                conn = http.client.HTTPConnection(args.host, args.port, timeout=15)
                status, images = -1, -1
            elapsed = (time.perf_counter() - t0) * 1000
            with lock:
                results.append((rnd, loc, status, round(elapsed, 1), images))
        conn.close()

    start = datetime.now()
    round_windows = []
    for plan in rounds:
        rnd = plan[0][0]
        if args.no_cache:
            # 매 라운드 전에 캐시를 비워 모든 요청이 TourAPI를 실제 호출하도록 함 (= 캐시 미적용 동작)
            flush_tourapi_cache(args)
        cursor[0] = 0
        slot[0] = time.perf_counter()
        round_start = datetime.now()
        threads = [threading.Thread(target=worker, args=(plan,)) for _ in range(args.concurrency)]
        for t in threads:
            t.start()
        for t in threads:
            t.join()
        round_windows.append((rnd, round_start, datetime.now()))
    end = datetime.now()

    out = os.path.join(OUT_ROOT, f"{args.label}-{start:%Y%m%d-%H%M%S}")
    os.makedirs(out, exist_ok=True)
    with open(os.path.join(out, "raw.csv"), "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(["round", "location_id", "status", "elapsed_ms", "image_count"])
        w.writerows(results)

    ok = [r for r in results if r[2] == 200]

    def row(name, vals):
        return (f"{name:<8} n={len(vals):>4} avg={statistics.mean(vals):7.1f} p50={pct(vals, 50):7.1f} "
                f"p95={pct(vals, 95):7.1f} p99={pct(vals, 99):7.1f} max={max(vals):7.1f}")

    mode = "no-cache(라운드마다 캐시 삭제)" if args.no_cache else ("cache(시작 전 1회 삭제)" if args.flush_before else "cache")
    lines = [
        f"label={args.label} target={args.target} mode={mode} host={args.host}:{args.port} "
        f"N={args.n} rounds={args.rounds} concurrency={args.concurrency} rps={args.rps or '-'} "
        f"requests={len(results)}",
        f"start={start:%Y-%m-%d %H:%M:%S} end={end:%Y-%m-%d %H:%M:%S} (Grafana 구간 조회용)",
        f"errors={len(results) - len(ok)}",
    ]
    if ok:
        lines.append(row("ALL", [r[3] for r in ok]))
        for rnd, r_start, r_end in round_windows:
            vals = [r[3] for r in ok if r[0] == rnd]
            if vals:
                lines.append(row(f"round{rnd}", vals) + f"  [{r_start:%H:%M:%S}~{r_end:%H:%M:%S}]")
    summary = "\n".join(lines)
    with open(os.path.join(out, "summary.txt"), "w", encoding="utf-8") as f:
        f.write(summary + "\n")
    print(summary)
    print("saved:", out)


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("label")
    p.add_argument("--target", choices=["tourapi", "curated"], default="tourapi")
    p.add_argument("--host", default="13.210.218.196")
    p.add_argument("--port", type=int, default=8080)
    p.add_argument("-n", type=int, default=50, help="대상 관광지 수")
    p.add_argument("-r", "--rounds", type=int, default=4)
    p.add_argument("-c", "--concurrency", type=int, default=5)
    p.add_argument("--rps", type=float, default=0, help="초당 요청 수 고정 (0이면 최대 속도). Grafana 구간을 길게 만들 때 사용")
    p.add_argument("--no-cache", action="store_true",
                   help="라운드마다 운영 Redis tourapi:* 캐시를 비워 매 요청 TourAPI를 호출 (캐시 미적용 측정)")
    p.add_argument("--flush-before", action="store_true",
                   help="시작 전에 한 번만 캐시를 비움 (캐시 적용 측정: 1라운드 miss -> 이후 hit)")
    p.add_argument("--ssh-user", default="ubuntu")
    run(p.parse_args())


if __name__ == "__main__":
    main()
