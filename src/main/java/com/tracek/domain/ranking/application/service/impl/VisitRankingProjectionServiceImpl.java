package com.tracek.domain.ranking.application.service.impl;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.ranking.domain.model.TargetId;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitRankingProjectionServiceImpl implements VisitRankingProjectionService {

    private final LocationVisitRankingRepository locationVisitRankingRepository;

    private final ArtistLocationVisitRankingRepository artistLocationVisitRankingRepository;

    private final ContentLocationVisitRankingRepository contentLocationVisitRankingRepository;

    private final ContentArtistVisitRankingRepository contentArtistVisitRankingRepository;

    private final ContentArtistLocationVisitRankingRepository
            contentArtistLocationVisitRankingRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increase(Long locationId, Long contentId, Set<Long> artistIds) {

        /*
         * 방문 인증 자체는 1건이므로
         * 아티스트 수와 관계없이 한 번만 증가
         */
        locationVisitRankingRepository.increaseVerificationCount(locationId);

        /*
         * location + content 역시
         * 방문 인증 1건 기준으로 한 번만 증가
         * (contentId가 없는 장소 단독 인증이면 스킵)
         */
        if (contentId == null) {
            return;
        }

        contentLocationVisitRankingRepository.increaseVerificationCount(locationId, contentId);

        /*
         * 아티스트와 관련된 랭킹만
         * 선택한 아티스트 각각 반영
         */
        for (Long artistId : artistIds == null ? Set.<Long>of() : artistIds) {

            artistLocationVisitRankingRepository.increaseVerificationCount(locationId, artistId);

            if (contentId != null) {
                contentArtistVisitRankingRepository.increaseVerificationCount(contentId, artistId);

                contentArtistLocationVisitRankingRepository.increaseVerificationCount(
                        new TargetId(locationId, contentId, artistId));
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long locationId, Long contentId, Set<Long> artistIds) {

        /*
         * 방문 인증 하나가 취소되므로
         * 한 번만 감소
         */
        locationVisitRankingRepository.decreaseVerificationCount(locationId);

        if (contentId == null) {
            return;
        }

        contentLocationVisitRankingRepository.decreaseVerificationCount(locationId, contentId);

        /*
         * 방문 인증에 연결돼 있던
         * 모든 아티스트 랭킹 감소
         */
        for (Long artistId : artistIds == null ? Set.<Long>of() : artistIds) {

            artistLocationVisitRankingRepository.decreaseVerificationCount(locationId, artistId);

            if (contentId != null) {
                contentArtistVisitRankingRepository.decreaseVerificationCount(contentId, artistId);

                contentArtistLocationVisitRankingRepository.decreaseVerificationCount(
                        new TargetId(locationId, contentId, artistId));
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void update(
            Long locationId,
            Long previousContentId,
            Set<Long> previousArtistIds,
            Long updatedContentId,
            Set<Long> updatedArtistIds) {

        boolean contentChanged = !Objects.equals(previousContentId, updatedContentId);

        Set<Long> removedArtistIds = new HashSet<>(previousArtistIds);

        removedArtistIds.removeAll(updatedArtistIds);

        Set<Long> addedArtistIds = new HashSet<>(updatedArtistIds);

        addedArtistIds.removeAll(previousArtistIds);

        boolean artistChanged = !removedArtistIds.isEmpty() || !addedArtistIds.isEmpty();

        if (!contentChanged && !artistChanged) {
            return;
        }
        if (contentChanged) {
            updateContentLocationRanking(locationId, previousContentId, updatedContentId);
            updateContentArtistRankingsWhenContentChanged(
                    previousContentId, previousArtistIds, updatedContentId, updatedArtistIds);

            updateContentArtistLocationRankingsWhenContentChanged(
                    locationId,
                    previousContentId,
                    previousArtistIds,
                    updatedContentId,
                    updatedArtistIds);

        } else if (artistChanged) {
            updateContentArtistRankingsWhenArtistChanged(
                    previousContentId, removedArtistIds, addedArtistIds);

            updateContentArtistLocationRankingsWhenArtistChanged(
                    locationId, previousContentId, removedArtistIds, addedArtistIds);
        }
        if (artistChanged) {
            updateArtistLocationRanking(locationId, removedArtistIds, addedArtistIds);
        }
    }

    private void updateContentLocationRanking(
            Long locationId, Long previousContentId, Long updatedContentId) {

        if (previousContentId != null) {
            contentLocationVisitRankingRepository.decreaseVerificationCount(
                    locationId, previousContentId);
        }

        if (updatedContentId != null) {
            contentLocationVisitRankingRepository.increaseVerificationCount(
                    locationId, updatedContentId);
        }
    }

    /**
     * Artist + Location
     *
     * <p>제거된 아티스트만 -1 추가된 아티스트만 +1
     */
    private void updateArtistLocationRanking(
            Long locationId, Set<Long> removedArtistIds, Set<Long> addedArtistIds) {

        for (Long artistId : removedArtistIds) {
            artistLocationVisitRankingRepository.decreaseVerificationCount(locationId, artistId);
        }

        for (Long artistId : addedArtistIds) {
            artistLocationVisitRankingRepository.increaseVerificationCount(locationId, artistId);
        }
    }

    /**
     * Content가 변경된 경우
     *
     * <p>Content + Artist는 기존 조합 전체 제거 후 새로운 조합 전체 추가
     */
    private void updateContentArtistRankingsWhenContentChanged(
            Long previousContentId,
            Set<Long> previousArtistIds,
            Long updatedContentId,
            Set<Long> updatedArtistIds) {

        if (previousContentId != null) {
            for (Long artistId : previousArtistIds) {
                contentArtistVisitRankingRepository.decreaseVerificationCount(
                        previousContentId, artistId);
            }
        }

        if (updatedContentId != null) {
            for (Long artistId : updatedArtistIds) {
                contentArtistVisitRankingRepository.increaseVerificationCount(
                        updatedContentId, artistId);
            }
        }
    }

    /** Content는 그대로이고 Artist만 변경된 경우 */
    private void updateContentArtistRankingsWhenArtistChanged(
            Long contentId, Set<Long> removedArtistIds, Set<Long> addedArtistIds) {

        if (contentId == null) {
            return;
        }

        for (Long artistId : removedArtistIds) {
            contentArtistVisitRankingRepository.decreaseVerificationCount(contentId, artistId);
        }

        for (Long artistId : addedArtistIds) {
            contentArtistVisitRankingRepository.increaseVerificationCount(contentId, artistId);
        }
    }

    /**
     * Content 변경 시 Location + Content + Artist
     *
     * <p>이전 조합 전체 제거 후 새로운 조합 전체 추가
     */
    private void updateContentArtistLocationRankingsWhenContentChanged(
            Long locationId,
            Long previousContentId,
            Set<Long> previousArtistIds,
            Long updatedContentId,
            Set<Long> updatedArtistIds) {

        if (previousContentId != null) {
            for (Long artistId : previousArtistIds) {
                contentArtistLocationVisitRankingRepository.decreaseVerificationCount(
                        new TargetId(locationId, previousContentId, artistId));
            }
        }

        if (updatedContentId != null) {
            for (Long artistId : updatedArtistIds) {
                contentArtistLocationVisitRankingRepository.increaseVerificationCount(
                        new TargetId(locationId, updatedContentId, artistId));
            }
        }
    }

    /** Content는 그대로이고 Artist만 변경된 경우 */
    private void updateContentArtistLocationRankingsWhenArtistChanged(
            Long locationId, Long contentId, Set<Long> removedArtistIds, Set<Long> addedArtistIds) {

        if (contentId == null) {
            return;
        }

        for (Long artistId : removedArtistIds) {
            contentArtistLocationVisitRankingRepository.decreaseVerificationCount(
                    new TargetId(locationId, contentId, artistId));
        }

        for (Long artistId : addedArtistIds) {
            contentArtistLocationVisitRankingRepository.increaseVerificationCount(
                    new TargetId(locationId, contentId, artistId));
        }
    }
}
