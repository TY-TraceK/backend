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

    public List<Long> getContentIdsByLocationId(Long locationId) {
        return queryFactory
                .select(episode.content.id)
                .distinct()
                .from(episodeLocation)
                .join(episodeLocation.episode, episode) // episode에 content_id 정보가 있음
                .where(episodeLocation.location.id.eq(locationId))
                .fetch();
    }

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
}
