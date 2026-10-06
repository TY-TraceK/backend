-- 지도 bounds 조회용 공간 인덱스(R-Tree).
-- latitude/longitude 로부터 자동 계산되는 평면 좌표(SRID 0, x=경도, y=위도) generated column을 추가하고
-- SPATIAL INDEX를 건다. 기존 latitude/longitude 컬럼과 idx_location_geo(B-Tree)는 그대로 유지한다
-- (방문 인증 거리 계산 등 기존 로직과, 넓은 범위 bounds 조회는 계속 B-Tree를 사용).
--
-- SRID 4326(구면) 대신 SRID 0(평면)을 쓰는 이유: 카카오맵 bounds는 위도/경도 축에 평행한 사각형이라
-- 평면 비교가 화면 영역과 정확히 일치하고(4326은 경계가 대원을 따라 휘어 화면 밖 점이 섞임),
-- 점마다 구면 계산을 하지 않아 실측상 2~3배 빠름 (scripts/jmeter/LOAD_TEST_LOG.md 참고).
--
-- STORED generated column이라 INSERT/UPDATE 시 MySQL이 자동 계산하며, 애플리케이션(엔티티)은 이 컬럼을 매핑하지 않는다.
-- 단, NOT NULL 이므로 latitude/longitude 가 NULL인 행은 INSERT가 실패한다 (적용 전 NULL 건수 0 확인 필요).
-- Hibernate ddl-auto/@Index로는 SPATIAL INDEX나 generated column을 만들 수 없어서
-- 이 DDL은 반드시 직접 실행해야 함 (로컬/배포 DB 각각). 재실행해도 안전하도록 이미 있으면 건너뜀.

SET @null_coords = (SELECT COUNT(*) FROM location WHERE latitude IS NULL OR longitude IS NULL);
SELECT @null_coords AS null_coordinate_rows_must_be_zero;

SET @col_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'location'
      AND column_name = 'geo_point'
);

SET @sql = IF(
    @col_exists = 0 AND @null_coords = 0,
    'ALTER TABLE location ADD COLUMN geo_point POINT AS (ST_SRID(POINT(longitude, latitude), 0)) STORED SRID 0 NOT NULL',
    'SELECT ''geo_point column already exists (or NULL coordinates found), skipping'' AS notice'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @idx_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'location'
      AND index_name = 'sidx_location_geo_point'
);

SET @col_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'location'
      AND column_name = 'geo_point'
);

SET @sql = IF(
    @idx_exists = 0 AND @col_exists = 1,
    'CREATE SPATIAL INDEX sidx_location_geo_point ON location (geo_point)',
    'SELECT ''sidx_location_geo_point already exists (or geo_point missing), skipping'' AS notice'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
