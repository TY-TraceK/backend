#!/usr/bin/env python3
"""지도 bounds 조회(GET /api/locations/bounds) 응답 크기/지연 측정 - 응답 압축(gzip) 적용 전/후 동일 조건 비교용.

- 브라우저처럼 Accept-Encoding: gzip 을 보내고, 실제로 네트워크로 받은 바이트(압축 상태 그대로)를 잰다.
- 부산시청 중심으로 지도 줌 단계를 흉내 낸 bounds 시나리오별로 R회씩 조회 (동시 C, 스레드별 keep-alive).
- 측정 지표: 받은 바이트, Content-Encoding, 결과 건수, TTFB(첫 바이트), total(본문 수신 완료)

사용 예:
  python bench-bounds.py before-gzip
  python bench-bounds.py after-gzip --host 13.210.218.196 --port 8080 -r 30 -c 5
  python bench-bounds.py before-gzip-cold --cold   # 매 요청 새 연결 (첫 접속/유휴 후 재접속)

결과: results/bounds/<label>-<시각>/{raw.csv, summary.txt}
"""
import argparse
import csv
import gzip
import http.client
import json
import os
import statistics
import threading
import time
from datetime import datetime

HERE = os.path.dirname(os.path.abspath(__file__))
OUT_ROOT = os.path.join(HERE, "results", "bounds")

CENTER_LAT, CENTER_LNG = 35.1796, 129.0756  # 부산광역시청 (LocationBoundsQuery 기본 폴백 기준점)
SPANS = [0.30, 0.15, 0.06, 0.02]  # 서버 MAX_SPAN = 0.3


def scenarios():
    yield "default", "/api/locations/bounds?archivedOnly=false"
    for span in SPANS:
        h = span / 2
        q = (f"southwestLatitude={CENTER_LAT - h:.4f}&southwestLongitude={CENTER_LNG - h:.4f}"
             f"&northeastLatitude={CENTER_LAT + h:.4f}&northeastLongitude={CENTER_LNG + h:.4f}&archivedOnly=false")
        yield f"span-{span:.2f}", f"/api/locations/bounds?{q}"


def pct(values, p):
    s = sorted(values)
    k = max(0, min(len(s) - 1, int(round(p / 100 * len(s) + 0.5)) - 1))
    return s[k]


def run(args):
    plan = [(name, path) for name, path in scenarios() for _ in range(args.rounds)]
    results, lock, cursor = [], threading.Lock(), [0]

    def worker():
        conn = http.client.HTTPConnection(args.host, args.port, timeout=20)
        while True:
            with lock:
                if cursor[0] >= len(plan):
                    break
                name, path = plan[cursor[0]]
                cursor[0] += 1
            try:
                if args.cold:
                    # 매 요청 새 TCP 연결 (첫 접속/유휴 후 재접속 상황: TCP slow start 영향 포함)
                    conn.close()
                    conn = http.client.HTTPConnection(args.host, args.port, timeout=20)
                t0 = time.perf_counter()
                conn.request("GET", path, headers={"Accept-Encoding": "gzip", "Accept": "application/json"})
                resp = conn.getresponse()
                ttfb = (time.perf_counter() - t0) * 1000
                raw = resp.read()  # 압축 상태 그대로의 바이트 (http.client는 자동 해제하지 않음)
                total = (time.perf_counter() - t0) * 1000
                encoding = resp.getheader("Content-Encoding") or "identity"
                body = gzip.decompress(raw) if encoding == "gzip" else raw
                rows = len(json.loads(body)["data"]["locations"]) if resp.status == 200 else -1
                row = (name, resp.status, encoding, len(raw), len(body), rows, round(ttfb, 1), round(total, 1))
            except Exception:
                conn.close()
                conn = http.client.HTTPConnection(args.host, args.port, timeout=20)
                row = (name, -1, "-", 0, 0, -1, 0.0, 0.0)
            with lock:
                results.append(row)
        conn.close()

    start = datetime.now()
    threads = [threading.Thread(target=worker) for _ in range(args.concurrency)]
    for t in threads:
        t.start()
    for t in threads:
        t.join()
    end = datetime.now()

    out = os.path.join(OUT_ROOT, f"{args.label}-{start:%Y%m%d-%H%M%S}")
    os.makedirs(out, exist_ok=True)
    with open(os.path.join(out, "raw.csv"), "w", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        w.writerow(["scenario", "status", "content_encoding", "wire_bytes", "body_bytes", "rows", "ttfb_ms", "total_ms"])
        w.writerows(results)

    lines = [
        f"label={args.label} host={args.host}:{args.port} conn={'cold(매 요청 새 연결)' if args.cold else 'keep-alive'} "
        f"rounds={args.rounds} concurrency={args.concurrency} "
        f"requests={len(results)} errors={sum(1 for r in results if r[1] != 200)}",
        f"start={start:%Y-%m-%d %H:%M:%S} end={end:%Y-%m-%d %H:%M:%S} (Grafana 구간 조회용)",
        f"{'scenario':<10} {'rows':>5} {'enc':>8} {'wire':>9} {'body':>9} "
        f"{'ttfb50':>7} {'tot50':>7} {'tot95':>7} {'tot99':>7}",
    ]
    for name, _ in scenarios():
        ok = [r for r in results if r[0] == name and r[1] == 200]
        if not ok:
            continue
        tot = [r[7] for r in ok]
        lines.append(
            f"{name:<10} {ok[0][5]:>5} {ok[0][2]:>8} {int(statistics.median(r[3] for r in ok)):>8}B "
            f"{int(statistics.median(r[4] for r in ok)):>8}B {pct([r[6] for r in ok], 50):>7.0f} "
            f"{pct(tot, 50):>7.0f} {pct(tot, 95):>7.0f} {pct(tot, 99):>7.0f}"
        )
    summary = "\n".join(lines)
    with open(os.path.join(out, "summary.txt"), "w", encoding="utf-8") as f:
        f.write(summary + "\n")
    print(summary)
    print("saved:", out)


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("label")
    p.add_argument("--host", default="13.210.218.196")
    p.add_argument("--port", type=int, default=8080)
    p.add_argument("-r", "--rounds", type=int, default=30, help="시나리오당 요청 수")
    p.add_argument("-c", "--concurrency", type=int, default=5)
    p.add_argument("--cold", action="store_true", help="매 요청 새 TCP 연결 (연결 수립 시간 포함, TCP slow start 영향 측정)")
    run(p.parse_args())


if __name__ == "__main__":
    main()
