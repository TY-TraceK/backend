package com.tracek.domain.image.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tracek.domain.image.domain.model.Image;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImageRepositoryImplTest {

    @Mock private ImageJpaRepository imageJpaRepository;

    private ImageRepositoryImpl imageRepositoryImpl;

    @BeforeEach
    void setUp() {
        imageRepositoryImpl = new ImageRepositoryImpl(imageJpaRepository);
    }

    @Test
    @DisplayName("findById는 ImageJpaRepository에 위임한다")
    void findById_delegates() {
        Image image = Image.create("http://image.com/a.jpg");
        given(imageJpaRepository.findById(1L)).willReturn(Optional.of(image));

        assertThat(imageRepositoryImpl.findById(1L)).contains(image);
    }

    @Test
    @DisplayName("findAll/save/deleteById는 ImageJpaRepository에 위임한다")
    void findAllSaveDelete_delegate() {
        Image image = Image.create("http://image.com/a.jpg");
        given(imageJpaRepository.findAll()).willReturn(List.of(image));
        given(imageJpaRepository.save(image)).willReturn(image);

        assertThat(imageRepositoryImpl.findAll()).containsExactly(image);
        assertThat(imageRepositoryImpl.save(image)).isEqualTo(image);

        imageRepositoryImpl.deleteById(1L);
        verify(imageJpaRepository).deleteById(1L);
    }
}
