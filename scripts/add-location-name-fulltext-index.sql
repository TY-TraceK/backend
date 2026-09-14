-- 통합검색(이름만 매칭)용 FULLTEXT 인덱스 (ngram 파서).
-- 기존 idx_location_search(name, city, district)와는 별개 - 통합검색은 지역명 매칭 없이
-- 관광지 이름 단독으로만 매칭해야 해서 name 단일 컬럼 인덱스를 따로 둠.
-- Hibernate ddl-auto/@Index로는 FULLTEXT나 WITH PARSER ngram을 만들 수 없어서
-- 이 DDL은 반드시 직접 실행해야 함 (로컬/배포 DB 각각).
-- 재실행해도 안전하도록 이미 있으면 건너뜀.

SET @idx_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'location'
      AND index_name = 'idx_location_name_search'
);

SET @sql = IF(
    @idx_exists = 0,
    'ALTER TABLE location ADD FULLTEXT INDEX idx_location_name_search (name) WITH PARSER ngram',
    'SELECT ''idx_location_name_search already exists, skipping'' AS notice'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
