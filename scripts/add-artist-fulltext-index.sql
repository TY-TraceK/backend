-- 아티스트 검색용 FULLTEXT 인덱스 (ngram 파서, 한글 부분 문자열 매칭 지원).
-- Hibernate ddl-auto/@Index로는 FULLTEXT나 WITH PARSER ngram을 만들 수 없어서
-- 이 DDL은 반드시 직접 실행해야 함 (로컬/배포 DB 각각).
-- 재실행해도 안전하도록 이미 있으면 건너뜀.
--
-- ngram_token_size는 서버 전역 변수라 인덱스 생성 전에 이미 반영돼 있어야 함
-- (location과 동일하게 기본값 2 사용).

SET @idx_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'artist'
      AND index_name = 'idx_artist_search'
);

SET @sql = IF(
    @idx_exists = 0,
    'ALTER TABLE artist ADD FULLTEXT INDEX idx_artist_search (name, alias) WITH PARSER ngram',
    'SELECT ''idx_artist_search already exists, skipping'' AS notice'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
