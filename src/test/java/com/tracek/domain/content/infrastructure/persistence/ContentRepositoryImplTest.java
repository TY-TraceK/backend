package com.tracek.domain.content.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tracek.domain.content.domain.model.Content;
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
class ContentRepositoryImplTest {

    @Mock private ContentJpaRepository contentJpaRepository;

    private ContentRepositoryImpl contentRepositoryImpl;

    @BeforeEach
    void setUp() {
        contentRepositoryImpl = new ContentRepositoryImpl(contentJpaRepository);
    }

    @Test
    @DisplayName("findById는 ContentJpaRepository에 위임한다")
    void findById_delegates() {
        Content content = Content.create("데뷔 앨범", "KPOP", ImageUrl.from("http://image.com/a.jpg"));
        given(contentJpaRepository.findById(1L)).willReturn(Optional.of(content));

        assertThat(contentRepositoryImpl.findById(1L)).contains(content);
    }

    @Test
    @DisplayName("findAllByIds는 ContentJpaRepository.findAllById에 위임한다")
    void findAllByIds_delegates() {
        Content content = Content.create("데뷔 앨범", "KPOP", ImageUrl.from("http://image.com/a.jpg"));
        given(contentJpaRepository.findAllById(List.of(1L))).willReturn(List.of(content));

        assertThat(contentRepositoryImpl.findAllByIds(List.of(1L))).containsExactly(content);
    }

    @Test
    @DisplayName("findAll/save/deleteById는 ContentJpaRepository에 위임한다")
    void findAllSaveDelete_delegate() {
        Content content = Content.create("데뷔 앨범", "KPOP", ImageUrl.from("http://image.com/a.jpg"));
        given(contentJpaRepository.findAll()).willReturn(List.of(content));
        given(contentJpaRepository.save(content)).willReturn(content);

        assertThat(contentRepositoryImpl.findAll()).containsExactly(content);
        assertThat(contentRepositoryImpl.save(content)).isEqualTo(content);

        contentRepositoryImpl.deleteById(1L);
        verify(contentJpaRepository).deleteById(1L);
    }
}
