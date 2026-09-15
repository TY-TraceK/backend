package com.tracek.domain.artist.infrasructure.persistence;

import com.tracek.domain.artist.domain.model.Artist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistJpaRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByGroupId(Long groupId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE Artist a SET a.totalVerificationCount = a.totalVerificationCount + 1 WHERE a.id = :id")
    void increseVerificationCount(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE Artist a SET a.totalVerificationCount = a.totalVerificationCount - 1 WHERE a.id = :id AND a.totalVerificationCount > 0")
    void decreseVerificationCount(@Param("id") Long id);
}
