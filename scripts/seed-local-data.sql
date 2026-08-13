-- 로컬 개발/Swagger 수동 테스트용 더미 데이터.
-- 앱 기동(ddl-auto: update)으로 테이블이 이미 생성된 뒤에 실행하세요.
-- 재실행 가능하도록 기존 더미 데이터를 먼저 지웁니다 (FK 역순 삭제).

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM location_content_artist;
DELETE FROM image_location;
DELETE FROM location;
DELETE FROM content;
DELETE FROM artist;
DELETE FROM image;
SET FOREIGN_KEY_CHECKS = 1;

-- ==================== 관광지 (location) ====================
INSERT INTO location (id, name, category, like_count, city, district, address, latitude, longitude, image_url) VALUES
(1, '경복궁', 'ATTRACTION', 1520, '서울특별시', '종로구', '사직로 161', 37.5796, 126.9770, 'https://picsum.photos/seed/loc1/800/600'),
(2, '남산타워', 'ATTRACTION', 980, '서울특별시', '용산구', '남산공원길 105', 37.5512, 126.9882, 'https://picsum.photos/seed/loc2/800/600'),
(3, '전주한옥마을', 'CULTURE', 760, '전라북도', '전주시 완산구', '기린대로 99', 35.8151, 127.1535, 'https://picsum.photos/seed/loc3/800/600'),
(4, '해운대해수욕장', 'ATTRACTION', 640, '부산광역시', '해운대구', '해운대해변로 264', 35.1587, 129.1604, 'https://picsum.photos/seed/loc4/800/600'),
(5, '성산일출봉', 'ATTRACTION', 410, '제주특별자치도', '서귀포시', '성산읍 성산리 1', 33.4585, 126.9425, 'https://picsum.photos/seed/loc5/800/600');

-- ==================== 이미지 (image) ====================
INSERT INTO image (id, image_url) VALUES
(1, 'https://picsum.photos/seed/img1/800/600'),
(2, 'https://picsum.photos/seed/img2/800/600'),
(3, 'https://picsum.photos/seed/img3/800/600'),
(4, 'https://picsum.photos/seed/img4/800/600'),
(5, 'https://picsum.photos/seed/img5/800/600'),
(6, 'https://picsum.photos/seed/img6/800/600'),
(7, 'https://picsum.photos/seed/img7/800/600'),
(8, 'https://picsum.photos/seed/img8/800/600');

-- 관광지별 이미지 매핑 (경복궁은 3장, 나머지는 각 1~2장)
INSERT INTO image_location (id, location_id, image_id, display_order, is_main) VALUES
(1, 1, 1, 1, true),
(2, 1, 2, 2, false),
(3, 1, 3, 3, false),
(4, 2, 4, 1, true),
(5, 3, 5, 1, true),
(6, 3, 6, 2, false),
(7, 4, 7, 1, true),
(8, 5, 8, 1, true);

-- ==================== 콘텐츠 (content) ====================
INSERT INTO content (id, title, category, image_url) VALUES
(1, '킹더랜드', 'DRAMA', 'https://picsum.photos/seed/content1/800/600'),
(2, '도깨비', 'DRAMA', 'https://picsum.photos/seed/content2/800/600'),
(3, '런닝맨', 'ENTERTAINMENT', 'https://picsum.photos/seed/content3/800/600'),
(4, 'Super Shy', 'MV', 'https://picsum.photos/seed/content4/800/600'),
(5, '1박 2일', 'VARIETY', 'https://picsum.photos/seed/content5/800/600'),
(6, '눈물의 여왕', 'DRAMA', 'https://picsum.photos/seed/content6/800/600');

-- ==================== 아티스트 (artist) ====================
-- group_id로 자기 참조 (뉴진스가 그룹, 민지/하니가 멤버)
INSERT INTO artist (id, name, alias, image_url, description, group_id) VALUES
(1, '아이유', 'IU', 'https://picsum.photos/seed/artist1/800/600', '가수 겸 배우', NULL),
(2, '뉴진스', 'NJZ', 'https://picsum.photos/seed/artist2/800/600', '4인조 걸그룹', NULL),
(3, '민지', NULL, 'https://picsum.photos/seed/artist3/800/600', '뉴진스 멤버', 2),
(4, '하니', NULL, 'https://picsum.photos/seed/artist4/800/600', '뉴진스 멤버', 2),
(5, '공유', NULL, 'https://picsum.photos/seed/artist5/800/600', '배우', NULL),
(6, '김지원', NULL, 'https://picsum.photos/seed/artist6/800/600', '배우', NULL);

-- ==================== 관광지-콘텐츠-아티스트 매핑 (location_content_artist) ====================
-- 경복궁(location_id=1)에 매핑을 몰아넣어서, 나중에 "연관 콘텐츠/아티스트 조회" N+1 vs 배치 조회
-- 성능 비교를 재현 가능하게 함. 나머지 관광지는 소량만.
INSERT INTO location_content_artist (location_id, content_id, artist_id) VALUES
(1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 1, 5), (1, 1, 6),
(1, 2, 1), (1, 2, 2), (1, 2, 3), (1, 2, 4), (1, 2, 5), (1, 2, 6),
(1, 3, 1), (1, 3, 2), (1, 3, 3), (1, 3, 4), (1, 3, 5), (1, 3, 6),
(1, 4, 2), (1, 4, 3), (1, 4, 4), (1, 5, 1), (1, 5, 5), (1, 6, 6),
(2, 2, 5), (2, 1, 6),
(3, 5, 1), (3, 5, 6),
(4, 3, 2), (4, 3, 3),
(5, 4, 2);
