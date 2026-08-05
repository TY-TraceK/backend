package com.tracek.domain.artist.infrasructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtistRepositoryImplTest {

    @Mock private ArtistJpaRepository artistJpaRepository;

    private ArtistRepositoryImpl artistRepositoryImpl;

    @BeforeEach
    void setUp() {
        artistRepositoryImpl = new ArtistRepositoryImpl(artistJpaRepository);
    }

    @Test
    @DisplayName("findById는 ArtistJpaRepository에 위임한다")
    void findById_delegates() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/a.jpg"), null, null);
        given(artistJpaRepository.findById(1L)).willReturn(Optional.of(artist));

        assertThat(artistRepositoryImpl.findById(1L)).contains(artist);
    }

    @Test
    @DisplayName("findAllByIds는 ArtistJpaRepository.findAllById에 위임한다")
    void findAllByIds_delegates() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/a.jpg"), null, null);
        given(artistJpaRepository.findAllById(List.of(1L))).willReturn(List.of(artist));

        assertThat(artistRepositoryImpl.findAllByIds(List.of(1L))).containsExactly(artist);
    }

    @Test
    @DisplayName("findAll/save/deleteById는 ArtistJpaRepository에 위임한다")
    void findAllSaveDelete_delegate() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/a.jpg"), null, null);
        given(artistJpaRepository.findAll()).willReturn(List.of(artist));
        given(artistJpaRepository.save(artist)).willReturn(artist);

        assertThat(artistRepositoryImpl.findAll()).containsExactly(artist);
        assertThat(artistRepositoryImpl.save(artist)).isEqualTo(artist);

        artistRepositoryImpl.deleteById(1L);
        verify(artistJpaRepository).deleteById(1L);
    }
}
