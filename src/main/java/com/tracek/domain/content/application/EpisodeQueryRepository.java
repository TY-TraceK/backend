package com.tracek.domain.content.application;

import static com.tracek.domain.content.domain.model.QEpisode.episode;
import static com.tracek.domain.content.domain.model.QEpisodeArtist.episodeArtist;
import static com.tracek.domain.content.domain.model.QEpisodeLocation.episodeLocation;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EpisodeQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ContentArtistPair> getContentGroupsByLocationId(Long locationId) {
        List<Tuple> rows =
                queryFactory
                        .select(episode.content.id, episodeArtist.artist.id)
                        .distinct()
                        .from(episodeLocation)
                        .join(episodeLocation.episode, episode) // episode -> content_id
                        .join(episodeArtist) // episodeArtist -> artist_id
                        .on(episode.id.eq(episodeArtist.episode.id))
                        .where(episodeLocation.location.id.eq(locationId))
                        .fetch();

        return rows.stream()
                .map(
                        t ->
                                ContentArtistPair.of(
                                        t.get(episode.content.id), t.get(episodeArtist.artist.id)))
                .toList();
    }

    public List<Long> getLocationIdsByContentId(Long contentId) {
        return queryFactory
                .select(episodeLocation.location.id)
                .distinct()
                .from(episodeLocation)
                .join(episodeLocation.episode, episode)
                .where(episode.content.id.eq(contentId))
                .fetch();
    }

    public List<Long> getLocationIdsByArtistId(Long artistId) {
        return queryFactory
                .select(episodeLocation.location.id)
                .distinct()
                .from(episodeLocation)
                .join(episodeLocation.episode, episode)
                .join(episodeArtist)
                .on(episode.id.eq(episodeArtist.episode.id))
                .where(episodeArtist.artist.id.eq(artistId))
                .fetch();
    }

    public Boolean isRelatedContent(Long locationId, Long contentId) {
        return queryFactory
                        .selectOne()
                        .from(episodeLocation)
                        .where(
                                episodeLocation.location.id.eq(locationId),
                                episodeLocation.episode.content.id.eq(contentId))
                        .fetchFirst()
                != null;
    }

    public Boolean isRelatedContentAndArtist(Long locationId, Long contentId, Long artistId) {
        return queryFactory
                        .selectOne()
                        .from(episodeLocation)
                        .join(episodeLocation.episode, episode)
                        .join(episodeArtist)
                        .on(episode.id.eq(episodeArtist.episode.id))
                        .where(
                                episodeLocation.location.id.eq(locationId),
                                episode.content.id.eq(contentId),
                                episodeArtist.artist.id.eq(artistId))
                        .fetchFirst()
                != null;
    }
}
