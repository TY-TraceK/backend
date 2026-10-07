#!/bin/bash
# tracek-flow-test.jmx(사용자 흐름 부하테스트)가 쓰는 입력 데이터를 DB에서 뽑아 CSV로 저장한다.
# 부하테스트 대상 서버와 같은 DB에서 뽑아야 존재하지 않는 ID로 404가 섞이지 않는다.
#
# 사용 예:
#   로컬 docker DB  : LOCAL_DB_PASSWORD=<root 비밀번호> ./export-flow-data.sh
#   다른 DB(EC2 등)  : MYSQL_CMD="mysql -h <host> -P 3306 -u <user> -p<pw> tracek" ./export-flow-data.sh
#
# 출력: scripts/jmeter/flow-data/{locations,hot_locations,curated_locations,contents,artists,keywords}.csv
set -euo pipefail

cd "$(dirname "$0")"
OUT=flow-data
mkdir -p "$OUT"
# 비밀번호를 스크립트에 남기지 않도록 환경변수로 받는다 (MYSQL_CMD를 직접 지정하면 필요 없음)
if [ -z "${MYSQL_CMD:-}" ]; then
    : "${LOCAL_DB_PASSWORD:?로컬 docker MySQL root 비밀번호를 LOCAL_DB_PASSWORD 환경변수로 지정하세요}"
    MYSQL_CMD="docker exec -i -e MYSQL_PWD=$LOCAL_DB_PASSWORD tracek-mysql mysql --default-character-set=utf8mb4 -uroot tracek"
fi

q() { $MYSQL_CMD -N -B -e "$1" 2>/dev/null; }

# 관광지: id, 위도, 경도 (지도 핀 클릭/방문인증 모달 좌표용)
q "SELECT id, latitude, longitude FROM location WHERE latitude BETWEEN 34.8 AND 35.5 AND longitude BETWEEN 128.7 AND 129.4" \
  | tr '\t' ',' > "$OUT/locations.csv"

# 인기 관광지: 방문 인증 수 상위 5개 (트래픽 쏠림 재현용)
q "SELECT l.id, l.latitude, l.longitude FROM location l JOIN location_ranking r ON r.location_id = l.id
   WHERE l.latitude BETWEEN 34.8 AND 35.5 AND l.longitude BETWEEN 128.7 AND 129.4
   ORDER BY r.total_visit_verification_count DESC, l.id LIMIT 5" \
  | tr '\t' ',' > "$OUT/hot_locations.csv"
if [ ! -s "$OUT/hot_locations.csv" ]; then
  head -5 "$OUT/locations.csv" > "$OUT/hot_locations.csv"
fi

# 관광지 상세 조회 대상: TourAPI(개발계정 1일 1000건)를 호출하지 않는 CURATED 관광지만
q "SELECT id FROM location WHERE source_type = 'CURATED' AND external_content_id IS NULL" > "$OUT/curated_locations.csv"

# 콘텐츠/아티스트: 촬영지가 1개 이상 연결된 것만 (상세 화면이 비지 않도록)
q "SELECT DISTINCT e.content_id FROM episode e JOIN episode_location el ON el.episode_id = e.id" > "$OUT/contents.csv"
q "SELECT DISTINCT ea.artist_id FROM episode_artist ea JOIN episode_location el ON el.episode_id = ea.episode_id" > "$OUT/artists.csv"

# 검색 키워드: 자주 쓰일 법한 장소 키워드 + 실제 아티스트 이름 + 콘텐츠 제목 앞 단어
{
  printf '%s\n' 부산 해수욕장 시장 카페 공원 해운대 광안리 전포 태종대 감천
  q "SELECT a.name FROM artist a JOIN episode_artist ea ON ea.artist_id = a.id
     GROUP BY a.id, a.name ORDER BY COUNT(*) DESC LIMIT 20"
  q "SELECT SUBSTRING_INDEX(title, ' ', 1) FROM content ORDER BY id DESC LIMIT 20"
} | awk 'length($0) >= 2' | sort -u > "$OUT/keywords.csv"

wc -l "$OUT"/*.csv
