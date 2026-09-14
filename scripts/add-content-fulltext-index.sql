-- 콘텐츠 검색용 FULLTEXT 인덱스 (ngram 파서, 한글 부분 문자열 매칭 지원). title 단일 컬럼 대상.
-- Hibernate ddl-auto/@Index로는 FULLTEXT나 WITH PARSER ngram을 만들 수 없어서
-- 이 DDL은 반드시 직접 실행해야 함 (로컬/배포 DB 각각).
-- 재실행해도 안전하도록 이미 있으면 건너뜀.

SET @idx_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'content'
      AND index_name = 'idx_content_search'
);

SET @sql = IF(
    @idx_exists = 0,
    'ALTER TABLE content ADD FULLTEXT INDEX idx_content_search (title) WITH PARSER ngram',
    'SELECT ''idx_content_search already exists, skipping'' AS notice'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
