-- JMeter 부하테스트용 더미 데이터.
-- seed-local-data.sql과 동일한 패턴, 다만 location/content/artist 수를 늘려서
-- 좋아요 부하테스트 시 여러 location_id에 스레드를 분산시킬 수 있게 함.
-- 앱 기동(ddl-auto: update)으로 테이블이 이미 생성된 뒤에 실행하세요.
-- 재실행 가능하도록 기존 더미 데이터를 먼저 지웁니다 (FK 역순 삭제).
--
-- users 테이블은 LocationLikeCommandService에 isActiveUser 검증이 추가되면서
-- 좋아요/취소 API가 실제로 조회합니다. 다만 배포 DB에는 이미 실사용자(팀원 등)
-- 계정이 있을 수 있어 기존 users는 절대 DELETE하지 않고, ID 900001~900200
-- 구간에만 부하테스트 전용 유저를 새로 추가합니다 (재실행해도 안전하도록 INSERT IGNORE).
-- JMeter에서 쓸 JWT는 이 ID 구간(900001~900200)으로 오프라인 서명하면 됩니다.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM location_likes;
DELETE FROM location_content_artist;
DELETE FROM image_location;
DELETE FROM location;
DELETE FROM content;
DELETE FROM artist;
DELETE FROM image;
SET FOREIGN_KEY_CHECKS = 1;

-- ==================== 관광지 (location) : 50개 ====================
-- id 1~40 : 서로 다른 location_id로 좋아요를 분산시키는 "일반 트래픽" 시나리오용
-- id 41~50 : 이름 뒤에 '(hot)' 표시 — 여러 스레드가 동일 location_id를 동시에 좋아요 눌러서
--            UNIQUE 제약/Lost Update 레이스 컨디션을 재현하는 "핫스팟" 시나리오용
INSERT INTO location (id, name, category, like_count, city, district, address, latitude, longitude, image_url) VALUES
(1, '경복궁', 'ATTRACTION', 1520, '서울특별시', '종로구', '사직로 161', 37.5796, 126.9770, 'https://picsum.photos/seed/loc1/800/600'),
(2, '남산타워', 'ATTRACTION', 980, '서울특별시', '용산구', '남산공원길 105', 37.5512, 126.9882, 'https://picsum.photos/seed/loc2/800/600'),
(3, '전주한옥마을', 'CULTURE', 760, '전라북도', '전주시 완산구', '기린대로 99', 35.8151, 127.1535, 'https://picsum.photos/seed/loc3/800/600'),
(4, '해운대해수욕장', 'ATTRACTION', 640, '부산광역시', '해운대구', '해운대해변로 264', 35.1587, 129.1604, 'https://picsum.photos/seed/loc4/800/600'),
(5, '성산일출봉', 'ATTRACTION', 410, '제주특별자치도', '서귀포시', '성산읍 성산리 1', 33.4585, 126.9425, 'https://picsum.photos/seed/loc5/800/600'),
(6, '광안리해수욕장', 'ATTRACTION', 512, '부산광역시', '수영구', '광안해변로 219', 35.1531, 129.1186, 'https://picsum.photos/seed/loc6/800/600'),
(7, '인사동', 'CULTURE', 388, '서울특별시', '종로구', '인사동길 62', 37.5740, 126.9857, 'https://picsum.photos/seed/loc7/800/600'),
(8, '경주 불국사', 'CULTURE', 701, '경상북도', '경주시', '불국로 385', 35.7898, 129.3320, 'https://picsum.photos/seed/loc8/800/600'),
(9, '순천만습지', 'ATTRACTION', 299, '전라남도', '순천시', '순천만길 513', 34.8798, 127.5039, 'https://picsum.photos/seed/loc9/800/600'),
(10, '속초 설악산', 'ATTRACTION', 845, '강원특별자치도', '속초시', '설악산로 833', 38.1194, 128.4656, 'https://picsum.photos/seed/loc10/800/600'),
(11, '을지로 골목', 'RESTAURANT', 210, '서울특별시', '중구', '을지로 157', 37.5663, 126.9917, 'https://picsum.photos/seed/loc11/800/600'),
(12, '홍대거리', 'FESTIVAL', 933, '서울특별시', '마포구', '홍익로 20', 37.5563, 126.9236, 'https://picsum.photos/seed/loc12/800/600'),
(13, '강릉 안목해변', 'ATTRACTION', 455, '강원특별자치도', '강릉시', '창해로 14', 37.7748, 128.9472, 'https://picsum.photos/seed/loc13/800/600'),
(14, '여수 오동도', 'ATTRACTION', 366, '전라남도', '여수시', '오동도로 60', 34.7492, 127.7622, 'https://picsum.photos/seed/loc14/800/600'),
(15, '통영 동피랑마을', 'CULTURE', 278, '경상남도', '통영시', '동피랑1길 6', 34.8433, 128.4335, 'https://picsum.photos/seed/loc15/800/600'),
(16, '북촌한옥마을', 'CULTURE', 664, '서울특별시', '종로구', '계동길 37', 37.5826, 126.9831, 'https://picsum.photos/seed/loc16/800/600'),
(17, '동대문디자인플라자', 'CULTURE', 592, '서울특별시', '중구', '을지로 281', 37.5665, 127.0092, 'https://picsum.photos/seed/loc17/800/600'),
(18, '수원화성', 'ATTRACTION', 431, '경기도', '수원시 팔달구', '정조로 825', 37.2850, 127.0093, 'https://picsum.photos/seed/loc18/800/600'),
(19, '남이섬', 'ATTRACTION', 877, '강원특별자치도', '춘천시', '남이섬길 1', 37.7904, 127.5253, 'https://picsum.photos/seed/loc19/800/600'),
(20, '거제 바람의언덕', 'ATTRACTION', 340, '경상남도', '거제시', '남부면 갈곶리', 34.7386, 128.6906, 'https://picsum.photos/seed/loc20/800/600'),
(21, '군산 근대문화거리', 'FILMING_LOCATION', 203, '전라북도', '군산시', '해망로 240', 35.9758, 126.7104, 'https://picsum.photos/seed/loc21/800/600'),
(22, '담양 죽녹원', 'ATTRACTION', 289, '전라남도', '담양군', '죽녹원로 119', 35.3216, 126.9887, 'https://picsum.photos/seed/loc22/800/600'),
(23, '대구 김광석다시그리기길', 'CULTURE', 356, '대구광역시', '중구', '달구벌대로 2238', 35.8621, 128.5993, 'https://picsum.photos/seed/loc23/800/600'),
(24, '전주 남부시장', 'RESTAURANT', 421, '전라북도', '전주시 완산구', '풍남문2길 63', 35.8113, 127.1470, 'https://picsum.photos/seed/loc24/800/600'),
(25, '인천 차이나타운', 'RESTAURANT', 498, '인천광역시', '중구', '차이나타운로 43', 37.4738, 126.6178, 'https://picsum.photos/seed/loc25/800/600'),
(26, '태안 안면도', 'ATTRACTION', 261, '충청남도', '태안군', '안면대로 1112', 36.5347, 126.3200, 'https://picsum.photos/seed/loc26/800/600'),
(27, '보성 녹차밭', 'ATTRACTION', 315, '전라남도', '보성군', '봇재로 763', 34.7717, 127.2410, 'https://picsum.photos/seed/loc27/800/600'),
(28, '단양 도담삼봉', 'ATTRACTION', 189, '충청북도', '단양군', '단양읍 도담리', 37.0089, 128.3651, 'https://picsum.photos/seed/loc28/800/600'),
(29, '포항 호미곶', 'ATTRACTION', 227, '경상북도', '포항시 남구', '호미곶면 대보리', 36.0764, 129.5678, 'https://picsum.photos/seed/loc29/800/600'),
(30, '양양 서핑비치', 'ATTRACTION', 512, '강원특별자치도', '양양군', '현북면 하조대해변길', 38.0763, 128.6402, 'https://picsum.photos/seed/loc30/800/600'),
(31, '서울숲', 'ATTRACTION', 604, '서울특별시', '성동구', '뚝섬로 273', 37.5443, 127.0374, 'https://picsum.photos/seed/loc31/800/600'),
(32, '월정리해변', 'ATTRACTION', 733, '제주특별자치도', '제주시', '월정1리 26', 33.5566, 126.7963, 'https://picsum.photos/seed/loc32/800/600'),
(33, '가로수길', 'FESTIVAL', 288, '서울특별시', '강남구', '가로수길', 37.5202, 127.0231, 'https://picsum.photos/seed/loc33/800/600'),
(34, '경복궁 서촌', 'CULTURE', 245, '서울특별시', '종로구', '자하문로 15길', 37.5804, 126.9689, 'https://picsum.photos/seed/loc34/800/600'),
(35, '전주 객리단길', 'RESTAURANT', 356, '전라북도', '전주시 완산구', '경fg원로 지역', 35.8110, 127.1420, 'https://picsum.photos/seed/loc35/800/600'),
(36, '부산 감천문화마을', 'CULTURE', 812, '부산광역시', '사하구', '감내2로 203', 35.0975, 129.0107, 'https://picsum.photos/seed/loc36/800/600'),
(37, '제주 협재해수욕장', 'ATTRACTION', 690, '제주특별자치도', '제주시', '협재리 2497', 33.3941, 126.2397, 'https://picsum.photos/seed/loc37/800/600'),
(38, '경주 첨성대', 'CULTURE', 401, '경상북도', '경주시', '첨성로 169', 35.8348, 129.2192, 'https://picsum.photos/seed/loc38/800/600'),
(39, '춘천 남춘천역거리', 'FILMING_LOCATION', 178, '강원특별자치도', '춘천시', '금강로', 37.8659, 127.7292, 'https://picsum.photos/seed/loc39/800/600'),
(40, '목포 근대역사문화거리', 'CULTURE', 234, '전라남도', '목포시', '번화로', 34.7936, 126.3819, 'https://picsum.photos/seed/loc40/800/600'),
(41, '핫스팟 좋아요 테스트 A (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 1', 37.5000, 127.0000, 'https://picsum.photos/seed/hot1/800/600'),
(42, '핫스팟 좋아요 테스트 B (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 2', 37.5001, 127.0001, 'https://picsum.photos/seed/hot2/800/600'),
(43, '핫스팟 좋아요 테스트 C (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 3', 37.5002, 127.0002, 'https://picsum.photos/seed/hot3/800/600'),
(44, '핫스팟 좋아요 테스트 D (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 4', 37.5003, 127.0003, 'https://picsum.photos/seed/hot4/800/600'),
(45, '핫스팟 좋아요 테스트 E (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 5', 37.5004, 127.0004, 'https://picsum.photos/seed/hot5/800/600'),
(46, '핫스팟 좋아요 테스트 F (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 6', 37.5005, 127.0005, 'https://picsum.photos/seed/hot6/800/600'),
(47, '핫스팟 좋아요 테스트 G (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 7', 37.5006, 127.0006, 'https://picsum.photos/seed/hot7/800/600'),
(48, '핫스팟 좋아요 테스트 H (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 8', 37.5007, 127.0007, 'https://picsum.photos/seed/hot8/800/600'),
(49, '핫스팟 좋아요 테스트 I (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 9', 37.5008, 127.0008, 'https://picsum.photos/seed/hot9/800/600'),
(50, '핫스팟 좋아요 테스트 J (hot)', 'ATTRACTION', 0, '테스트', '테스트구', '테스트로 10', 37.5009, 127.0009, 'https://picsum.photos/seed/hot10/800/600');

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

INSERT INTO image_location (id, location_id, image_id, display_order, is_main) VALUES
(1, 1, 1, 1, true),
(2, 1, 2, 2, false),
(3, 1, 3, 3, false),
(4, 2, 4, 1, true),
(5, 3, 5, 1, true),
(6, 3, 6, 2, false),
(7, 4, 7, 1, true),
(8, 5, 8, 1, true);

-- ==================== 콘텐츠 (content) : 15개 ====================
INSERT INTO content (id, title, category, image_url) VALUES
(1, '킹더랜드', 'DRAMA', 'https://picsum.photos/seed/content1/800/600'),
(2, '도깨비', 'DRAMA', 'https://picsum.photos/seed/content2/800/600'),
(3, '런닝맨', 'ENTERTAINMENT', 'https://picsum.photos/seed/content3/800/600'),
(4, 'Super Shy', 'MOVIE', 'https://picsum.photos/seed/content4/800/600'),
(5, '1박 2일', 'ENTERTAINMENT', 'https://picsum.photos/seed/content5/800/600'),
(6, '눈물의 여왕', 'DRAMA', 'https://picsum.photos/seed/content6/800/600'),
(7, '부산행', 'MOVIE', 'https://picsum.photos/seed/content7/800/600'),
(8, '슬기로운 의사생활', 'DRAMA', 'https://picsum.photos/seed/content8/800/600'),
(9, '무한도전', 'ENTERTAINMENT', 'https://picsum.photos/seed/content9/800/600'),
(10, '기생충', 'MOVIE', 'https://picsum.photos/seed/content10/800/600'),
(11, '나 혼자 산다', 'ENTERTAINMENT', 'https://picsum.photos/seed/content11/800/600'),
(12, '이태원 클라쓰', 'DRAMA', 'https://picsum.photos/seed/content12/800/600'),
(13, '신과함께', 'MOVIE', 'https://picsum.photos/seed/content13/800/600'),
(14, '유퀴즈온더블럭', 'ENTERTAINMENT', 'https://picsum.photos/seed/content14/800/600'),
(15, '지금 우리 학교는', 'DRAMA', 'https://picsum.photos/seed/content15/800/600');

-- ==================== 아티스트 (artist) : 15개 ====================
INSERT INTO artist (id, name, alias, image_url, description, group_id) VALUES
(1, '아이유', 'IU', 'https://picsum.photos/seed/artist1/800/600', '가수 겸 배우', NULL),
(2, '뉴진스', 'NJZ', 'https://picsum.photos/seed/artist2/800/600', '4인조 걸그룹', NULL),
(3, '민지', NULL, 'https://picsum.photos/seed/artist3/800/600', '뉴진스 멤버', 2),
(4, '하니', NULL, 'https://picsum.photos/seed/artist4/800/600', '뉴진스 멤버', 2),
(5, '공유', NULL, 'https://picsum.photos/seed/artist5/800/600', '배우', NULL),
(6, '김지원', NULL, 'https://picsum.photos/seed/artist6/800/600', '배우', NULL),
(7, '방탄소년단', 'BTS', 'https://picsum.photos/seed/artist7/800/600', '7인조 보이그룹', NULL),
(8, '정국', NULL, 'https://picsum.photos/seed/artist8/800/600', '방탄소년단 멤버', 7),
(9, '박서준', NULL, 'https://picsum.photos/seed/artist9/800/600', '배우', NULL),
(10, '송강', NULL, 'https://picsum.photos/seed/artist10/800/600', '배우', NULL),
(11, '유재석', NULL, 'https://picsum.photos/seed/artist11/800/600', '방송인', NULL),
(12, '아이브', 'IVE', 'https://picsum.photos/seed/artist12/800/600', '6인조 걸그룹', NULL),
(13, '장원영', NULL, 'https://picsum.photos/seed/artist13/800/600', '아이브 멤버', 12),
(14, '송중기', NULL, 'https://picsum.photos/seed/artist14/800/600', '배우', NULL),
(15, '전지현', NULL, 'https://picsum.photos/seed/artist15/800/600', '배우', NULL);

-- ==================== 관광지-콘텐츠-아티스트 매핑 ====================
-- 경복궁(location_id=1)에 매핑을 몰아넣어서 N+1 vs 배치 조회 성능 비교 재현 유지.
-- 나머지는 넓게 퍼뜨려서 목록/상세 조회 부하테스트 시 응답 payload 크기가 골고루 섞이게 함.
INSERT INTO location_content_artist (location_id, content_id, artist_id) VALUES
(1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 1, 5), (1, 1, 6),
(1, 2, 1), (1, 2, 2), (1, 2, 3), (1, 2, 4), (1, 2, 5), (1, 2, 6),
(1, 3, 1), (1, 3, 2), (1, 3, 3), (1, 3, 4), (1, 3, 5), (1, 3, 6),
(1, 4, 2), (1, 4, 3), (1, 4, 4), (1, 5, 1), (1, 5, 5), (1, 6, 6),
(2, 2, 5), (2, 1, 6),
(3, 5, 1), (3, 5, 6),
(4, 3, 2), (4, 3, 3),
(5, 4, 2),
(6, 7, 9), (7, 8, 10), (8, 9, 11), (9, 10, 12), (10, 11, 13),
(11, 12, 14), (12, 13, 15), (13, 14, 7), (14, 15, 8), (15, 1, 9),
(16, 2, 10), (17, 3, 11), (18, 4, 12), (19, 5, 13), (20, 6, 14),
(21, 7, 15), (22, 8, 1), (23, 9, 2), (24, 10, 3), (25, 11, 4),
(26, 12, 5), (27, 13, 6), (28, 14, 7), (29, 15, 8), (30, 1, 9);

-- ==================== 부하테스트 전용 유저 (users) : 900001~900200 ====================
-- 기존 users는 절대 건드리지 않음 (DELETE 없음). INSERT IGNORE라 재실행해도 안전.
-- JMeter용 JWT를 이 200명 userId로 오프라인 서명해서 CSV로 돌리면 됨.
INSERT IGNORE INTO users
    (id, provider_id, o_auth_provider, nickname, profile_image_url,
     connected_at, user_role, user_status, created_by, updated_by, created_at, updated_at)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 200
)
SELECT
    900000 + n,
    900000 + n,
    'KAKAO',
    CONCAT('부하테스트유저', n),
    CONCAT('https://picsum.photos/seed/loaduser', n, '/200/200'),
    NOW(),
    'USER',
    'ACTIVE',
    NULL,
    NULL,
    NOW(),
    NOW()
FROM seq;
