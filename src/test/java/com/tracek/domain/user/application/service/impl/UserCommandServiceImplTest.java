package com.tracek.domain.user.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.tracek.domain.user.application.dto.command.SyncUserCommand;
import com.tracek.domain.user.domain.enums.OAuthProvider;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import com.tracek.domain.user.domain.model.UserProfile;
import com.tracek.domain.user.domain.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserCommandServiceImpl userCommandService;

    private SyncUserCommand createCommand(
            Long providerId, String providerName, String nickname, String imageUrl) {
        return new SyncUserCommand(
                providerId, providerName, nickname, imageUrl, OffsetDateTime.now());
    }

    @Nested
    @DisplayName("registerOrUpdateUser 테스트")
    class RegisterOrUpdateUserTest {

        @Test
        @DisplayName("신규 유저인 경우 새로운 User 엔티티를 생성하고 저장한다")
        void registerNewUser() {
            // given
            SyncUserCommand command =
                    createCommand(12345L, "KAKAO", "testUser", "http://image.com");
            OAuthInfo oAuthInfo = OAuthInfo.register(Long.valueOf("12345"), OAuthProvider.KAKAO);
            UserProfile userProfile = UserProfile.register("testUser", "http://image.com");
            User newUser = User.createUser(oAuthInfo, command.connectedAt(), userProfile);
            ReflectionTestUtils.setField(newUser, "id", 1L);

            given(userRepository.findByOAuthInfo(any(OAuthInfo.class)))
                    .willReturn(Optional.empty());
            given(userRepository.save(any(User.class))).willReturn(newUser);

            // when
            Long userId = userCommandService.registerOrUpdateUser(command);

            // then
            assertThat(userId).isEqualTo(1L);
            then(userRepository).should(times(1)).findByOAuthInfo(any(OAuthInfo.class));
            then(userRepository).should(times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("기존 유저인 경우 프로필 정보를 업데이트한다")
        void updateExistingUser() {
            // given
            SyncUserCommand command =
                    createCommand(12345L, "KAKAO", "updatedNick", "http://new-image.com");
            OAuthInfo oAuthInfo = OAuthInfo.register(Long.valueOf("12345"), OAuthProvider.KAKAO);
            UserProfile oldProfile = UserProfile.register("oldNick", "http://old-image.com");
            User existingUser = User.createUser(oAuthInfo, command.connectedAt(), oldProfile);
            ReflectionTestUtils.setField(existingUser, "id", 1L);

            given(userRepository.findByOAuthInfo(any(OAuthInfo.class)))
                    .willReturn(Optional.of(existingUser));

            // when
            Long userId = userCommandService.registerOrUpdateUser(command);

            // then
            assertThat(userId).isEqualTo(1L);
            then(userRepository).should(times(1)).findByOAuthInfo(any(OAuthInfo.class));
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("잘못된 OAuthProvider 이름이 들어오면 IllegalArgumentException이 발생한다")
        void throwExceptionWhenInvalidProvider() {
            // given
            SyncUserCommand command =
                    createCommand(12345L, "INVALID_PROVIDER", "testUser", "http://image.com");

            // when & then
            assertThatThrownBy(() -> userCommandService.registerOrUpdateUser(command))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
