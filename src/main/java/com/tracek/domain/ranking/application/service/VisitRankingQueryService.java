package com.tracek.domain.ranking.application.service;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;

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
}
