TRUNCATE TABLE visit_verification;
TRUNCATE TABLE content_artist_location_ranking;
TRUNCATE TABLE artist_location_ranking;
TRUNCATE TABLE content_location_ranking;
TRUNCATE TABLE content_artist_ranking;
TRUNCATE TABLE location_ranking;

INSERT IGNORE INTO location_ranking (location_id,
                                     total_visit_verification_count)
SELECT id,
       0
FROM location;


-- CONTENT - ARTIST
-- content_artist가 기준
INSERT IGNORE INTO content_artist_ranking (content_id,
                                           artist_id,
                                           total_visit_verification_count)
SELECT content_id,
       artist_id,
       0
FROM content_artist;


-- CONTENT - LOCATION
-- Episode 관계 기준
INSERT IGNORE INTO content_location_ranking (content_id,
                                             location_id,
                                             total_visit_verification_count)
SELECT DISTINCT e.content_id,
                el.location_id,
                0
FROM episode e
         JOIN episode_location el
              ON el.episode_id = e.id;


-- ARTIST - LOCATION
-- 같은 Episode에 실제 존재하는 관계만
INSERT IGNORE INTO artist_location_ranking (artist_id,
                                            location_id,
                                            total_visit_verification_count)
SELECT DISTINCT ea.artist_id,
                el.location_id,
                0
FROM episode_artist ea
         JOIN episode_location el
              ON el.episode_id = ea.episode_id;


-- CONTENT - ARTIST - LOCATION
-- 같은 Episode에 실제 존재하는 조합만
INSERT IGNORE INTO content_artist_location_ranking (content_id,
                                                    artist_id,
                                                    location_id,
                                                    total_visit_verification_count)
SELECT DISTINCT e.content_id,
                ea.artist_id,
                el.location_id,
                0
FROM episode e
         JOIN episode_artist ea
              ON ea.episode_id = e.id
         JOIN episode_location el
              ON el.episode_id = e.id;