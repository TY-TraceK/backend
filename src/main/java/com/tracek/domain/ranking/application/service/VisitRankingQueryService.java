package com.tracek.domain.ranking.application.service;

import com.tracek.domain.ranking.application.dto.condition.LocationRankingCondition;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.condition.RegionRankingCondition;
import com.tracek.domain.ranking.application.dto.result.ContentCurationResult;
import com.tracek.domain.ranking.application.dto.result.LocationRankingResult;
import com.tracek.domain.ranking.application.dto.result.LocationRegionRankingResult;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RankingTopResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedMultiRankingResult;

public interface VisitRankingQueryService {

    // 아티스트 -> 연관된 콘텐츠 조회
    RankingSliceResult<RelatedContentRankingResult> getContentsByArtist(
            Long artistId, RankingCondition condition);

    // 아티스트 -> 연관된 장소 조회
    RankingSliceResult<RelatedLocationRankingResult> getLocationsByArtist(
            Long artistId, RankingCondition condition);

    // 장소 -> 연관된 콘텐츠 조회
    RankingSliceResult<RelatedContentRankingResult> getContentsByLocation(
            Long locationId, RankingCondition condition);

    // 장소 -> 연관된 아티스트 조회
    RankingSliceResult<RelatedArtistRankingResult> getArtistsByLocation(
            Long locationId, RankingCondition condition);

    // 콘텐츠 -> 연관 아티스트 조회
    RankingSliceResult<RelatedArtistRankingResult> getArtistsByContent(
            Long contentId, RankingCondition condition);

    // 콘텐츠 -> 연관 장소 조회
    RankingSliceResult<RelatedLocationRankingResult> getLocationsByContent(
            Long contentId, RankingCondition condition);

    // 콘텐츠 -> 전체 연관 랭킹 조회
    RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByContent(Long contentId);

    // 아티스트 -> 전체 연관 랭킹 조회
    RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByArtist(Long artistId);

    // 장소 -> 전체 연관 랭킹 조회
    RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByLocation(Long locationId);

    // 방문 인증이 적은 콘텐츠 여행 큐레이션 조회
    ContentCurationResult getLowVisitContentCuration();

    // 지역별 랭킹 조회
    RankingTopResult<LocationRegionRankingResult> getRegionRanking(
            RegionRankingCondition condition);

    // 관광지 랭킹 조회
    RankingTopResult<LocationRankingResult> getLocationRanking(LocationRankingCondition condition);
}
