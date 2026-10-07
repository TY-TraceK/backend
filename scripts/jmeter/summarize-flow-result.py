#!/usr/bin/env python3
"""사용자 흐름 부하테스트 결과(.jtl)를 성능 목표(p95 <= 300ms, p99 <= 500ms, 에러율 < 1%)와 대조한다.

- 램프업 구간은 사용자가 덜 찬 상태라 판정에서 뺀다 (--ramp 초).
- 00_토큰발급 / setUp_ 은 테스트 하네스라 제외한다.
- API별 표는 p95가 나쁜 순으로 정렬해서 병목 후보가 위로 오게 한다.

사용 예: python summarize-flow-result.py results/flow/avg-.../result.jtl --ramp 120
"""
import argparse
import csv
from collections import defaultdict

P95_TARGET = 300
P99_TARGET = 500
ERROR_TARGET = 1.0
EXCLUDE_PREFIX = ("00_토큰발급", "setUp_")


def pct(sorted_values, p):
    if not sorted_values:
        return 0
    k = max(0, min(len(sorted_values) - 1, int(round(p / 100 * len(sorted_values) + 0.5)) - 1))
    return sorted_values[k]


def stats(rows):
    elapsed = sorted(int(r["elapsed"]) for r in rows)
    errors = sum(1 for r in rows if r["success"] != "true")
    return {
        "n": len(rows),
        "err": errors,
        "err_pct": errors / len(rows) * 100 if rows else 0,
        "avg": sum(elapsed) / len(elapsed) if elapsed else 0,
        "p50": pct(elapsed, 50),
        "p95": pct(elapsed, 95),
        "p99": pct(elapsed, 99),
        "max": elapsed[-1] if elapsed else 0,
    }


def verdict(s):
    ok = s["p95"] <= P95_TARGET and s["p99"] <= P99_TARGET and s["err_pct"] < ERROR_TARGET
    return "PASS" if ok else "FAIL"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("jtl")
    ap.add_argument("--ramp", type=int, default=0, help="판정에서 뺄 램프업 구간(초)")
    args = ap.parse_args()

    with open(args.jtl, encoding="utf-8", newline="") as f:
        rows = [r for r in csv.DictReader(f) if not r["label"].startswith(EXCLUDE_PREFIX)]
    if not rows:
        print("집계할 요청이 없습니다.")
        return

    start = min(int(r["timeStamp"]) for r in rows)
    end = max(int(r["timeStamp"]) + int(r["elapsed"]) for r in rows)
    steady = [r for r in rows if int(r["timeStamp"]) >= start + args.ramp * 1000] or rows
    steady_sec = max(1, (end - (start + args.ramp * 1000)) / 1000)

    total = stats(steady)
    print(f"판정 구간: 램프업 {args.ramp}s 제외, {steady_sec:.0f}s / 요청 {total['n']}건 / "
          f"실측 처리량 {total['n'] / steady_sec:.1f} RPS")
    print(f"목표: p95 <= {P95_TARGET}ms, p99 <= {P99_TARGET}ms, 에러율 < {ERROR_TARGET}%\n")
    print(f"[전체] {verdict(total)}  p50={total['p50']}ms p95={total['p95']}ms p99={total['p99']}ms "
          f"max={total['max']}ms 에러={total['err']}건({total['err_pct']:.2f}%)\n")

    by_label = defaultdict(list)
    for r in steady:
        by_label[r["label"]].append(r)
    table = sorted(((label, stats(rs)) for label, rs in by_label.items()), key=lambda x: -x[1]["p95"])

    print(f"{'API':<34} {'건수':>6} {'에러%':>6} {'p50':>6} {'p95':>6} {'p99':>6} {'max':>7}  판정")
    for label, s in table:
        print(f"{label:<34} {s['n']:>6} {s['err_pct']:>6.2f} {s['p50']:>6} {s['p95']:>6} {s['p99']:>6} "
              f"{s['max']:>7}  {verdict(s)}")

    failures = defaultdict(int)
    for r in steady:
        if r["success"] != "true":
            failures[(r["label"], r["responseCode"], (r.get("failureMessage") or "")[:60])] += 1
    if failures:
        print("\n실패 상위:")
        for (label, code, msg), n in sorted(failures.items(), key=lambda x: -x[1])[:10]:
            print(f"  {n:>5}건  {label}  HTTP {code}  {msg}")


if __name__ == "__main__":
    main()
