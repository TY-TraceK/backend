package com.tracek.domain.ranking.application.service.impl;

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
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitRankingQueryServiceImpl implements VisitRankingQueryService {

    private final ContentArtistVisitRankingRepository contentArtistRankingRepository;
    private final ContentLocationVisitRankingRepository contentLocationRankingRepository;
    private final ArtistLocationVisitRankingRepository artistLocationRankingRepository;
    private final ContentArtistLocationVisitRankingRepository
            contentArtistLocationVisitRankingRepository;
    private final LocationVisitRankingRepository locationVisitRankingRepository;

    @Override
    public RankingSliceResult<RelatedArtistRankingResult> getArtistsByContent(
            Long contentId, RankingCondition condition) {

        return RankingSliceResult.from(
                contentArtistRankingRepository.findArtistsByContent(
                        contentId, condition.toCriteria()),
                condition.size(),
                item ->
                        new RelatedArtistRankingResult(
                                item.targetId().artistId(), item.totalVerificationCount()),
                item -> item.targetId().artistId());
    }

    @Override
    public RankingSliceResult<RelatedLocationRankingResult> getLocationsByContent(
            Long contentId, RankingCondition condition) {

        return RankingSliceResult.from(
                contentLocationRankingRepository.findLocationsByContent(
                        contentId, condition.toCriteria()),
                condition.size(),
                item ->
                        new RelatedLocationRankingResult(
                                item.targetId().locationId(), item.totalVerificationCount()),
                item -> item.targetId().locationId());
    }

    @Override
    public RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByContent(Long contentId) {

        List<RankingItem> items =
                contentArtistLocationVisitRankingRepository.findRankingsByContent(contentId, null);

        return RankingSliceResult.from(
                items,
                items.size(),
                item ->
                        RelatedMultiRankingResult.from(
                                item.targetId(), item.totalVerificationCount()),
                item -> item.targetId().locationId());
    }

    @Override
    public RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByArtist(Long artistId) {

        List<RankingItem> items =
                contentArtistLocationVisitRankingRepository.findRankingsByArtist(artistId, null);

        return RankingSliceResult.from(
                items,
                items.size(),
                item ->
                        RelatedMultiRankingResult.from(
                                item.targetId(), item.totalVerificationCount()),
                item -> item.targetId().locationId());
    }

    @Override
    public RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByLocation(
            Long locationId) {

        List<RankingItem> items =
                contentArtistLocationVisitRankingRepository.findRankingsByLocation(
                        locationId, null);

        return RankingSliceResult.from(
                items,
                items.size(),
                item ->
                        RelatedMultiRankingResult.from(
                                item.targetId(), item.totalVerificationCount()),
                item -> item.targetId().locationId());
    }

    @Override
    public RankingSliceResult<RelatedLocationRankingResult> getLocationsByArtist(
            Long artistId, RankingCondition condition) {

        return RankingSliceResult.from(
                artistLocationRankingRepository.findLocationsByArtist(
                        artistId, condition.toCriteria()),
                condition.size(),
                item ->
                        new RelatedLocationRankingResult(
                                item.targetId().locationId(), item.totalVerificationCount()),
                item -> item.targetId().locationId());
    }

    @Override
    public RankingSliceResult<RelatedContentRankingResult> getContentsByArtist(
            Long artistId, RankingCondition condition) {

        return RankingSliceResult.from(
                contentArtistRankingRepository.findContentsByArtist(
                        artistId, condition.toCriteria()),
                condition.size(),
                item ->
                        new RelatedContentRankingResult(
                                item.targetId().contentId(), item.totalVerificationCount()),
                item -> item.targetId().contentId());
    }

    @Override
    public RankingSliceResult<RelatedArtistRankingResult> getArtistsByLocation(
            Long locationId, RankingCondition condition) {

        return RankingSliceResult.from(
                artistLocationRankingRepository.findArtistsByLocation(
                        locationId, condition.toCriteria()),
                condition.size(),
                item ->
                        new RelatedArtistRankingResult(
                                item.targetId().artistId(), item.totalVerificationCount()),
                item -> item.targetId().artistId());
    }

    @Override
    public RankingSliceResult<RelatedContentRankingResult> getContentsByLocation(
            Long locationId, RankingCondition condition) {
        return RankingSliceResult.from(
                contentLocationRankingRepository.findContentsByLocation(
                        locationId, condition.toCriteria()),
                condition.size(),
                item ->
                        new RelatedContentRankingResult(
                                item.targetId().contentId(), item.totalVerificationCount()),
                item -> item.targetId().contentId());
    }

    @Override
    public ContentCurationResult getLowVisitContentCuration() {
        return contentLocationRankingRepository
                .findLowVisitContentCuration()
                .map(ContentCurationResult::from)
                .orElse(null);
    }

    @Override
    public RankingTopResult<LocationRegionRankingResult> getRegionRanking(
            RegionRankingCondition condition) {
        return RankingTopResult.from(
                locationVisitRankingRepository.findLocationRankingsByRegion(condition.toCriteria()),
                LocationRegionRankingResult::from);
    }

    @Override
    public RankingTopResult<LocationRankingResult> getLocationRanking(
            LocationRankingCondition condition) {
        return RankingTopResult.from(
                locationVisitRankingRepository.findLocationRankings(condition.toCriteria()),
                LocationRankingResult::from);
    }
}
