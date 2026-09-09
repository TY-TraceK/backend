package com.tracek.domain.ranking.application.service.impl;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import java.util.List;
import java.util.function.Function;
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

    @Override
    public RankingSliceResult<RelatedArtistRankingResult> getArtistsByContent(
            Long contentId, RankingCondition condition) {
        RankingSearchCriteria criteria = toCriteria(condition);

        List<RankingItem> items =
                contentArtistRankingRepository.findArtistsByContent(contentId, criteria);

        return toResult(
                items,
                condition.size(),
                item ->
                        new RelatedArtistRankingResult(
                                item.targetId(), item.totalVerificationCount()));
    }

    @Override
    public RankingSliceResult<RelatedLocationRankingResult> getLocationsByContent(
            Long contentId, RankingCondition condition) {
        RankingSearchCriteria criteria = toCriteria(condition);

        List<RankingItem> items =
                contentLocationRankingRepository.findLocationsByContent(contentId, criteria);

        return toResult(
                items,
                condition.size(),
                item ->
                        new RelatedLocationRankingResult(
                                item.targetId(), item.totalVerificationCount()));
    }

    @Override
    public RankingSliceResult<RelatedLocationRankingResult> getLocationsByArtist(
            Long artistId, RankingCondition condition) {
        RankingSearchCriteria criteria = toCriteria(condition);

        List<RankingItem> items =
                artistLocationRankingRepository.findLocationsByArtist(artistId, criteria);

        return toResult(
                items,
                condition.size(),
                item ->
                        new RelatedLocationRankingResult(
                                item.targetId(), item.totalVerificationCount()));
    }

    @Override
    public RankingSliceResult<RelatedContentRankingResult> getContentsByArtist(
            Long artistId, RankingCondition condition) {
        RankingSearchCriteria criteria = toCriteria(condition);

        List<RankingItem> items =
                contentArtistRankingRepository.findContentsByArtist(artistId, criteria);

        return toResult(
                items,
                condition.size(),
                item ->
                        new RelatedContentRankingResult(
                                item.targetId(), item.totalVerificationCount()));
    }

    @Override
    public RankingSliceResult<RelatedArtistRankingResult> getArtistsByLocation(
            Long locationId, RankingCondition condition) {
        RankingSearchCriteria criteria = toCriteria(condition);

        List<RankingItem> items =
                artistLocationRankingRepository.findArtistsByLocation(locationId, criteria);

        return toResult(
                items,
                condition.size(),
                item ->
                        new RelatedArtistRankingResult(
                                item.targetId(), item.totalVerificationCount()));
    }

    @Override
    public RankingSliceResult<RelatedContentRankingResult> getContentsByLocation(
            Long locationId, RankingCondition condition) {
        RankingSearchCriteria criteria = toCriteria(condition);

        List<RankingItem> items =
                contentLocationRankingRepository.findContentsByLocation(locationId, criteria);

        return toResult(
                items,
                condition.size(),
                item ->
                        new RelatedContentRankingResult(
                                item.targetId(), item.totalVerificationCount()));
    }

    private RankingSearchCriteria toCriteria(RankingCondition condition) {
        return new RankingSearchCriteria(
                condition.lastCount(), condition.lastId(), condition.size() + 1);
    }

    private <T> RankingSliceResult<T> toResult(
            List<RankingItem> items, int size, Function<RankingItem, T> mapper) {
        boolean hasNext = items.size() > size;

        List<RankingItem> slicedItems = hasNext ? items.subList(0, size) : items;

        List<T> results = slicedItems.stream().map(mapper).toList();

        if (slicedItems.isEmpty()) {
            return new RankingSliceResult<>(results, null, null, false);
        }

        RankingItem last = slicedItems.get(slicedItems.size() - 1);

        return new RankingSliceResult<>(
                results, last.totalVerificationCount(), last.targetId(), hasNext);
    }
}
