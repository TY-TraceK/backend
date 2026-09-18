package com.tracek.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.tracek.domain.user.domain.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock UserJpaRepository userJpaRepository;
    @Mock UserQueryDslRepository userQueryDslRepository;

    @Test
    void delegatesRepositoryOperations() {
        var repository = new UserRepositoryImpl(userJpaRepository, userQueryDslRepository);

        assertThat(repository.findByOAuthInfo(null)).isEmpty();
        assertThat(repository.findByIdAndUserStatusIs(1L, UserStatus.ACTIVE)).isFalse();
        assertThat(repository.findById(1L)).isEmpty();
        assertThat(repository.save(null)).isNull();
        assertThat(repository.findUserActivity(1L)).isNull();

        verify(userJpaRepository).findByoAuthInfo(null);
        verify(userJpaRepository).existsByIdAndUserStatusIs(1L, UserStatus.ACTIVE);
        verify(userJpaRepository).findById(1L);
        verify(userJpaRepository).save(null);
        verify(userQueryDslRepository).findUserActivity(1L);
    }
}
