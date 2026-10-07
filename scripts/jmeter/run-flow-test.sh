#!/bin/bash
# 사용자 흐름 부하테스트 실행기. 모드별 부하는 기획 '성능 목표' 문서 기준.
#
#   smoke  :   5VU /  2.0 RPS /  1분  - 시나리오가 끝까지 도는지 확인 (로컬용)
#   avg    : 140VU /  9.7 RPS / 20분  - 평균 시나리오(낙관)
#   peak   : 560VU / 46.7 RPS / 30분  - 피크 시나리오(비관)
#   stress : 560VU / RPS(기본 93.4 = 피크 x2) / 10분 - 목표 초과 구간에서 한계점 찾기
#
# 사용 예: ./run-flow-test.sh avg 13.210.218.196
#          RPS=140 ./run-flow-test.sh stress 13.210.218.196
# 결과: results/flow/<모드>-<시각>/ (result.jtl, report/, summary.txt)
set -euo pipefail

cd "$(dirname "$0")"
MODE=${1:?"모드를 지정하세요: smoke|avg|peak|stress"}
HOST=${2:-localhost}
PORT=${PORT:-8080}
JMETER=${JMETER:-jmeter}

case "$MODE" in
  smoke)  USERS=5;   TARGET_RPS=${RPS:-2};          DURATION=60;   RAMP=10  ;;
  avg)    USERS=140; TARGET_RPS=${RPS:-9.7};      DURATION=1200; RAMP=120 ;;
  peak)   USERS=560; TARGET_RPS=${RPS:-46.7};      DURATION=1800; RAMP=300 ;;
  stress) USERS=560; TARGET_RPS=${RPS:-93.4}; DURATION=600;  RAMP=120 ;;
  *) echo "알 수 없는 모드: $MODE" >&2; exit 1 ;;
esac

OUT="results/flow/${MODE}-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$OUT"
echo "[$MODE] HOST=$HOST:$PORT USERS=$USERS TARGET_RPS=$TARGET_RPS DURATION=${DURATION}s RAMP=${RAMP}s -> $OUT"
echo "시작: $(date '+%F %T')  (Grafana 구간 조회용으로 기록해두세요)" | tee "$OUT/timeline.txt"

"$JMETER" -n -t tracek-flow-test.jmx \
  -JHOST="$HOST" -JPORT="$PORT" \
  -JUSERS="$USERS" -JTARGET_RPS="$TARGET_RPS" -JDURATION="$DURATION" -JRAMP="$RAMP" \
  -JUSER_ID_BASE="${USER_ID_BASE:-0}" \
  -l "$OUT/result.jtl" -e -o "$OUT/report" -j "$OUT/jmeter.log"

echo "종료: $(date '+%F %T')" | tee -a "$OUT/timeline.txt"
PYTHONIOENCODING=utf-8 python summarize-flow-result.py "$OUT/result.jtl" --ramp "$RAMP" | tee "$OUT/summary.txt"
