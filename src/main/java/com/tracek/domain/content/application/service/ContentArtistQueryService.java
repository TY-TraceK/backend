package com.tracek.domain.content.application.service;

import com.tracek.domain.content.domain.model.ContentArtist;
import com.tracek.domain.content.domain.repository.ContentArtistRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentArtistQueryService {
    private final ContentArtistRepository contentArtistRepository;

    public List<Long> findContentIdsByArtistId(Long artistId) {
        return contentArtistRepository.findContentIdsByArtistId(artistId);
    }

    public List<Long> findArtistIdsByContentId(Long contentId) {
        return contentArtistRepository.findArtistIdsByContentId(contentId);
    }

    // contentId -> isFixed(고정 출연 여부)
    public Map<Long, Boolean> findIsFixedByArtistIdAndContentIds(
            Long artistId, List<Long> contentIds) {
        return contentArtistRepository.findByArtistIdAndContentIds(artistId, contentIds).stream()
                .collect(
                        Collectors.toMap(
                                contentArtist -> contentArtist.getContent().getId(),
                                ContentArtist::getIsFixed));
    }

    // 이 콘텐츠의 고정 출연 아티스트 id 목록
    public List<Long> findFixedArtistIdsByContentId(Long contentId) {
        return contentArtistRepository.findFixedByContentId(contentId).stream()
                .map(contentArtist -> contentArtist.getArtist().getId())
                .toList();
    }

    // contentId -> (artistId -> isFixed) 배치 조회
    public Map<Long, Map<Long, Boolean>> findIsFixedByContentIds(List<Long> contentIds) {
        return contentArtistRepository.findByContentIds(contentIds).stream()
                .collect(
                        Collectors.groupingBy(
                                contentArtist -> contentArtist.getContent().getId(),
                                Collectors.toMap(
                                        contentArtist -> contentArtist.getArtist().getId(),
                                        ContentArtist::getIsFixed)));
    }
}
