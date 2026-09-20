package com.tracek.domain.location.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.application.service.ImageQueryService;
import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.location.application.client.TourImageClient;
import com.tracek.domain.location.application.client.TourLocationDetailClient;
import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.dto.LocationSummaryResult;
import com.tracek.domain.location.application.dto.LocationTopSavedResult;
import com.tracek.domain.location.application.dto.TourImageResult;
import com.tracek.domain.location.application.dto.TourLocationDetailResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.ImageLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LocationFacadeTest {

    @Mock private LocationQueryService locationQueryService;
    @Mock private ContentQueryService contentQueryService;
    @Mock private ArtistQueryService artistQueryService;
    @Mock private ImageQueryService imageQueryService;
    @Mock private EpisodeQueryService episodeQueryService;
    @Mock private VisitRankingQueryService visitRankingQueryService;
    @Mock private ContentArtistQueryService contentArtistQueryService;
    @Mock private TourImageClient tourImageClient;
    @Mock private TourLocationDetailClient tourLocationDetailClient;

    private LocationFacade locationFacade;

    @BeforeEach
    void setUp() {
        locationFacade =
                new LocationFacade(
                        locationQueryService,
                        contentQueryService,
                        artistQueryService,
                        imageQueryService,
                        episodeQueryService,
                        visitRankingQueryService,
                        contentArtistQueryService,
                        tourImageClient,
                        tourLocationDetailClient);
    }

    private void stubEmptyRelatedRankings(RankingCondition condition) {
        given(visitRankingQueryService.getContentsByLocation(1L, condition))
                .willReturn(new RankingSliceResult<>(List.of(), null, null, false));
        given(visitRankingQueryService.getArtistsByLocation(1L, condition))
                .willReturn(new RankingSliceResult<>(List.of(), null, null, false));
        given(contentQueryService.getContentsByIds(List.of())).willReturn(List.of());
        given(artistQueryService.getArtistsByIds(List.of())).willReturn(List.of());
    }

    @Test
    @DisplayName("관광지 상세 조회 시 이미지/콘텐츠별 아티스트가 계층형으로 조립된다")
    void getLocationDetails_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Image image = Image.create("http://image.com/gyeongbok.jpg");
        ReflectionTestUtils.setField(image, "id", 5L);
        ImageLocation.create(location, image, 1, true);

        Content content =
                Content.create(
                        "궁궐 브이로그",
                        "ENTERTAINMENT",
                        "궁궐 브이로그 콘텐츠 소개",
                        ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 3L);

        RankingCondition condition = new RankingCondition(null, null, 20);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(imageQueryService.getImagesByIds(List.of(5L)))
                .willReturn(List.of(ImageResult.from(image)));
        given(visitRankingQueryService.getContentsByLocation(1L, condition))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedContentRankingResult(2L, 10L)),
                                null,
                                null,
                                false));
        given(visitRankingQueryService.getArtistsByLocation(1L, condition))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedArtistRankingResult(3L, 5L)),
                                null,
                                null,
                                false));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(List.of(ContentResult.from(content)));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(ArtistResult.from(artist)));

        LocationDetailResult result = locationFacade.getLocationDetails(1L, condition, null);

        assertThat(result.getLocationInfo().getId()).isEqualTo(1L);
        assertThat(result.getLocationInfo().getName()).isEqualTo("경복궁");
        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getImages().get(0).getImageUrl())
                .isEqualTo("http://image.com/gyeongbok.jpg");
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getArtists()).hasSize(1);
        assertThat(result.getArtists().get(0).getArtistName()).isEqualTo("아이유");
        verifyNoInteractions(tourImageClient, tourLocationDetailClient);
    }

    @Test
    @DisplayName("연관 콘텐츠/아티스트 매핑이 없으면 빈 리스트로 조립된다")
    void getLocationDetails_withoutMappings() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        RankingCondition condition = new RankingCondition(null, null, 20);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(imageQueryService.getImagesByIds(List.of())).willReturn(List.of());
        given(visitRankingQueryService.getContentsByLocation(1L, condition))
                .willReturn(new RankingSliceResult<>(List.of(), null, null, false));
        given(visitRankingQueryService.getArtistsByLocation(1L, condition))
                .willReturn(new RankingSliceResult<>(List.of(), null, null, false));
        given(contentQueryService.getContentsByIds(List.of())).willReturn(List.of());
        given(artistQueryService.getArtistsByIds(List.of())).willReturn(List.of());

        LocationDetailResult result = locationFacade.getLocationDetails(1L, condition, null);

        assertThat(result.getImages()).isEmpty();
        assertThat(result.getContents()).isEmpty();
    }

    @Test
    @DisplayName("TOUR_API 장소는 실시간 API로 사진/개요/전화번호를 받아온다")
    void getLocationDetails_tourApiSuccess_usesLiveData() {
        Location location =
                LocationTestFixture.newTourApiLocation(
                        1L, "부산타워", "ATTRACTION", 100L, 1277679L, "DB 개요", "051-000-0000");
        RankingCondition condition = new RankingCondition(null, null, 20);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(tourImageClient.getImages(1277679L))
                .willReturn(
                        List.of(
                                TourImageResult.of(
                                        "http://tour.api/img1.jpg", "http://tour.api/img1_s.jpg"),
                                TourImageResult.of(
                                        "http://tour.api/img2.jpg", "http://tour.api/img2_s.jpg")));
        given(tourLocationDetailClient.getDetail(1277679L))
                .willReturn(TourLocationDetailResult.of("실시간 API 개요", "051-111-2222"));
        stubEmptyRelatedRankings(condition);

        LocationDetailResult result = locationFacade.getLocationDetails(1L, condition, null);

        assertThat(result.getImages()).hasSize(2);
        assertThat(result.getImages().get(0).getImageId()).isNull();
        assertThat(result.getImages().get(0).getImageUrl()).isEqualTo("http://tour.api/img1.jpg");
        assertThat(result.getImages().get(0).getIsMain()).isTrue();
        assertThat(result.getImages().get(1).getIsMain()).isFalse();
        assertThat(result.getLocationInfo().getOverview()).isEqualTo("실시간 API 개요");
        assertThat(result.getLocationInfo().getTel()).isEqualTo("051-111-2222");
        verifyNoInteractions(imageQueryService);
    }

    @Test
    @DisplayName("TOUR_API 장소라도 API 호출이 실패하면 DB 값으로 폴백한다")
    void getLocationDetails_tourApiFailure_fallsBackToDb() {
        Location location =
                LocationTestFixture.newTourApiLocation(
                        1L, "부산타워", "ATTRACTION", 100L, 1277679L, "DB 개요", "051-000-0000");
        Image image = Image.create("http://image.com/busantower.jpg");
        ReflectionTestUtils.setField(image, "id", 5L);
        ImageLocation.create(location, image, 1, true);
        RankingCondition condition = new RankingCondition(null, null, 20);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(tourImageClient.getImages(1277679L))
                .willThrow(new IllegalStateException("TourAPI 호출 실패"));
        given(tourLocationDetailClient.getDetail(1277679L))
                .willThrow(new IllegalStateException("TourAPI 호출 실패"));
        given(imageQueryService.getImagesByIds(List.of(5L)))
                .willReturn(List.of(ImageResult.from(image)));
        stubEmptyRelatedRankings(condition);

        LocationDetailResult result = locationFacade.getLocationDetails(1L, condition, null);

        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getImages().get(0).getImageId()).isEqualTo(5L);
        assertThat(result.getImages().get(0).getImageUrl())
                .isEqualTo("http://image.com/busantower.jpg");
        assertThat(result.getLocationInfo().getOverview()).isEqualTo("DB 개요");
        assertThat(result.getLocationInfo().getTel()).isEqualTo("051-000-0000");
    }

    @Test
    @DisplayName("API 응답 중 일부 필드가 비어있으면 그 필드만 DB 값으로 채운다")
    void getLocationDetails_tourApiPartialBlank_fallsBackPerField() {
        Location location =
                LocationTestFixture.newTourApiLocation(
                        1L, "부산타워", "ATTRACTION", 100L, 1277679L, "DB 개요", "051-000-0000");
        RankingCondition condition = new RankingCondition(null, null, 20);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(tourImageClient.getImages(1277679L)).willReturn(List.of());
        given(tourLocationDetailClient.getDetail(1277679L))
                .willReturn(TourLocationDetailResult.of("", "051-111-2222"));
        given(imageQueryService.getImagesByIds(List.of())).willReturn(List.of());
        stubEmptyRelatedRankings(condition);

        LocationDetailResult result = locationFacade.getLocationDetails(1L, condition, null);

        assertThat(result.getImages()).isEmpty();
        assertThat(result.getLocationInfo().getOverview()).isEqualTo("DB 개요");
        assertThat(result.getLocationInfo().getTel()).isEqualTo("051-111-2222");
    }

    @Test
    @DisplayName("관광지 관련 콘텐츠-아티스트 정보를 배치 조회로 조립한다")
    void getRelatedContentAndArtists_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);

        Content content =
                Content.create(
                        "궁궐 브이로그",
                        "ENTERTAINMENT",
                        "궁궐 브이로그 콘텐츠 소개",
                        ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 3L);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(episodeQueryService.getContentArtistPairs(1L))
                .willReturn(List.of(ContentArtistPair.of(2L, 3L)));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(List.of(ContentResult.from(content)));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(ArtistResult.from(artist)));
        given(contentArtistQueryService.findIsFixedByContentIds(List.of(2L)))
                .willReturn(Map.of(2L, Map.of(3L, true)));

        LocationRelatedInfoResult result = locationFacade.getRelatedContentAndArtists(1L);

        assertThat(result.getLocationId()).isEqualTo(1L);
        assertThat(result.getRelatedContentGroups()).hasSize(1);
        assertThat(result.getRelatedContentGroups().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getRelatedContentGroups().get(0).getRelatedArtists()).hasSize(1);
        assertThat(
                        result.getRelatedContentGroups()
                                .get(0)
                                .getRelatedArtists()
                                .get(0)
                                .getArtistName())
                .isEqualTo("아이유");
        assertThat(result.getRelatedContentGroups().get(0).getRelatedArtists().get(0).getIsFixed())
                .isTrue();
    }

    @Test
    @DisplayName("TOP N 관광지 조회 시 연관 콘텐츠 top3와 로그인 유저의 북마크 여부가 함께 조립된다")
    void getTopSavedLocations_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Content content =
                Content.create(
                        "궁궐 브이로그",
                        "ENTERTAINMENT",
                        "궁궐 브이로그 콘텐츠 소개",
                        ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        RankingCondition top3Condition = new RankingCondition(null, null, 3);

        given(locationQueryService.getTopSavedLocations(5))
                .willReturn(List.of(LocationSummaryResult.from(location)));
        given(visitRankingQueryService.getContentsByLocation(1L, top3Condition))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedContentRankingResult(2L, 10L)),
                                null,
                                null,
                                false));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(List.of(ContentResult.from(content)));
        given(locationQueryService.isArchivedByUser(9L, 1L)).willReturn(true);

        List<LocationTopSavedResult> results = locationFacade.getTopSavedLocations(9L, 5);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(1L);
        assertThat(results.get(0).getRelatedContentTitles()).containsExactly("궁궐 브이로그");
        assertThat(results.get(0).getIsArchived()).isTrue();
    }
}
