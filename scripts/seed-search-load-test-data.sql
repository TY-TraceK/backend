-- 검색(LIKE vs full text index) 부하테스트용 대량 더미 데이터.
-- 지금 location 테이블에 5건밖에 없어서 인덱스 유무 차이가 전혀 안 보임 -> 대량 생성.
-- seed-load-test-data.sql(좋아요 부하테스트, id 1~50)과 겹치지 않도록 id 200001번대를 씀.
-- 재실행 가능하도록 기존 더미 데이터를 이 스크립트가 만드는 id 구간만 지우고 다시 넣음.
-- 앱 기동(ddl-auto: update)으로 테이블이 이미 생성된 뒤에 실행하세요.
--
-- location: 100,000건 (검색 대상 본체)
-- content/artist: 2,000건씩, image: 5,000건 -- location과 엮여있는 상세조회/목록조회
-- 부하테스트도 같이 굴릴 수 있게 넉넉히 생성 (location_content_artist, image_location으로 연결)
-- 모든 관계 테이블을 100,000건 전부와 엮으면 너무 커져서, 상세조회용 매핑은 앞쪽
-- location 5,000건(200001~205000)에만 연결함.
--
-- name은 "지역 + 장소유형 + 번호"로 조합해서 만듦 (예: '부산 해수욕장 00482').
-- 장소유형 단어가 20종류뿐이라 100,000건 기준 한 키워드당 매칭 건수가 수천 건대로
-- 나오게 되는데, 이게 실제 인기 키워드 검색(전체 스캔 vs 인덱스 스캔) 상황과 비슷함.

SET SESSION cte_max_recursion_depth = 200000;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM location_content_artist WHERE location_id BETWEEN 200001 AND 300000;
DELETE FROM image_location WHERE location_id BETWEEN 200001 AND 300000;
DELETE FROM location WHERE id BETWEEN 200001 AND 300000;
DELETE FROM content WHERE id BETWEEN 200001 AND 202000;
DELETE FROM artist WHERE id BETWEEN 200001 AND 202000;
DELETE FROM image WHERE id BETWEEN 200001 AND 205000;
SET FOREIGN_KEY_CHECKS = 1;

-- ==================== 관광지 (location) : 100,000개 (id 200001~300000) ====================
INSERT INTO location (id, name, category, like_count, city, district, address, latitude, longitude, image_url)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 100000
)
SELECT
    200000 + n,
    CONCAT(region, ' ', place_type, ' ', LPAD(n, 6, '0')),
    category,
    FLOOR(RAND() * 2000),
    region,
    CONCAT(district_name, '구'),
    CONCAT(region, ' ', district_name, '구 테스트로 ', n),
    33.0 + (RAND() * 5.5),
    126.0 + (RAND() * 3.5),
    CONCAT('https://picsum.photos/seed/loc', 200000 + n, '/800/600')
FROM (
    SELECT
        n,
        ELT(1 + MOD(n, 17), '서울', '부산', '대구', '인천', '광주', '대전', '울산', '세종',
            '경기', '강원', '충북', '충남', '전북', '전남', '경북', '경남', '제주') AS region,
        ELT(1 + MOD(FLOOR(n / 17), 20), '해수욕장', '타워', '한옥마을', '시장', '카페거리',
            '전망대', '폭포', '수목원', '미술관', '박물관', '성곽', '계곡', '등대', '항구',
            '야시장', '스카이워크', '출렁다리', '갈대숲', '벚꽃길', '케이블카') AS place_type,
        ELT(1 + MOD(n, 9), 'ATTRACTION', 'CULTURE', 'FESTIVAL', 'FILMING_LOCATION',
            'RESTAURANT', 'CAFE', 'ACCOMMODATION', 'SHOPPING', 'ETC') AS category,
        ELT(1 + MOD(FLOOR(n / 3), 10), '중', '종로', '강남', '해운대', '수영', '완산',
            '달서', '유성', '흥덕', '의창') AS district_name
    FROM seq
) t;

-- ==================== 콘텐츠 (content) : 2,000개 (id 200001~202000) ====================
INSERT INTO content (id, title, category, image_url)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 2000
)
SELECT
    200000 + n,
    CONCAT(title_word, ' ', n, '화'),
    category,
    CONCAT('https://picsum.photos/seed/content', 200000 + n, '/800/600')
FROM (
    SELECT
        n,
        ELT(1 + MOD(n, 8), '해변의 그날', '도시남녀', '골목식당', '한밤의 인터뷰',
            '섬마을 일기', '청춘기록', '전국여행', '여름밤') AS title_word,
        ELT(1 + MOD(n, 6), 'KPOP', 'DRAMA', 'MOVIE', 'ENTERTAINMENT', 'WEBTOON', 'ETC') AS category
    FROM seq
) t;

-- ==================== 아티스트 (artist) : 2,000개 (id 200001~202000) ====================
INSERT INTO artist (id, name, alias, image_url, description, group_id)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 2000
)
SELECT
    200000 + n,
    CONCAT(name_part, n),
    NULL,
    CONCAT('https://picsum.photos/seed/artist', 200000 + n, '/800/600'),
    '부하테스트용 더미 아티스트',
    NULL
FROM (
    SELECT
        n,
        ELT(1 + MOD(n, 6), '배우', '가수', '모델', '방송인', '댄서', '뮤지션') AS name_part
    FROM seq
) t;

-- ==================== 이미지 (image) : 5,000개 (id 200001~205000) ====================
INSERT INTO image (id, image_url)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 5000
)
SELECT 200000 + n, CONCAT('https://picsum.photos/seed/img', 200000 + n, '/800/600')
FROM seq;

-- ==================== 관광지-이미지 매핑 : 앞쪽 location 5,000건에 대표이미지 1개씩 ====================
INSERT INTO image_location (id, location_id, image_id, display_order, is_main)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 5000
)
SELECT 200000 + n, 200000 + n, 200000 + n, 1, TRUE
FROM seq;

-- ==================== 관광지-콘텐츠-아티스트 매핑 : 앞쪽 location 5,000건에 2건씩 ====================
INSERT INTO location_content_artist (id, location_id, content_id, artist_id)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 10000
)
SELECT
    200000 + n,
    200000 + CEIL(n / 2),
    200000 + 1 + MOD(n, 2000),
    200000 + 1 + MOD(n * 7, 2000)
FROM seq;
