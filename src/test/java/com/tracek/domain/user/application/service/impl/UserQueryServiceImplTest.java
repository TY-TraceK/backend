package com.tracek.domain.user.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.tracek.domain.user.application.dto.result.UserProfileDataResult;
import com.tracek.domain.user.domain.enums.OAuthProvider;
import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.domain.user.domain.exception.UserErrorCode;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import com.tracek.domain.user.domain.model.UserProfile;
import com.tracek.domain.user.domain.repository.UserRepository;
import com.tracek.global.exception.CustomException;
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
class UserQueryServiceImplTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserQueryServiceImpl userQueryService;

    private User createDummyUser(Long id) {
        OAuthInfo oAuthInfo = OAuthInfo.register(Long.valueOf("12345"), OAuthProvider.KAKAO);
        UserProfile userProfile = UserProfile.register("nick", "http://img.com");
        User user = User.createUser(oAuthInfo, OffsetDateTime.now(), userProfile);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @Nested
    @DisplayName("existsUserById 테스트")
    class ExistsUserByIdTest {

        @Test
        @DisplayName("존재하는 유저 ID인 경우 true를 반환한다")
        void returnTrueWhenUserExists() {
            // given
            Long userId = 1L;
            User user = createDummyUser(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            boolean result = userQueryService.existsUserById(userId);

            // then
            assertThat(result).isTrue();
            then(userRepository).should(times(1)).findById(userId);
        }

        @Test
        @DisplayName("존재하지 않는 유저 ID인 경우 false를 반환한다")
        void returnFalseWhenUserNotExists() {
            // given
            Long userId = 999L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when
            boolean result = userQueryService.existsUserById(userId);

            // then
            assertThat(result).isFalse();
            then(userRepository).should(times(1)).findById(userId);
        }
    }

    @Nested
    @DisplayName("isActiveUser 테스트")
    class IsActiveUserTest {

        @Test
        @DisplayName("ACTIVE 상태의 유저인 경우 true를 반환한다")
        void returnTrueWhenActive() {
            // given
            Long userId = 1L;
            given(userRepository.findByIdAndUserStatusIs(userId, UserStatus.ACTIVE))
                    .willReturn(true);

            // when
            boolean result = userQueryService.isActiveUser(userId);

            // then
            assertThat(result).isTrue();
            then(userRepository)
                    .should(times(1))
                    .findByIdAndUserStatusIs(userId, UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("ACTIVE 상태가 아니거나 존재하지 않는 유저인 경우 false를 반환한다")
        void returnFalseWhenNotActive() {
            // given
            Long userId = 1L;
            given(userRepository.findByIdAndUserStatusIs(userId, UserStatus.ACTIVE))
                    .willReturn(false);

            // when
            boolean result = userQueryService.isActiveUser(userId);

            // then
            assertThat(result).isFalse();
            then(userRepository)
                    .should(times(1))
                    .findByIdAndUserStatusIs(userId, UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("getUserProfileData 테스트")
    class GetUserProfileDataTest {

        @Test
        @DisplayName("유저가 존재하면 UserProfileDataResult를 정상 반환한다")
        void returnUserProfileData() {
            // given
            Long userId = 1L;
            User user = createDummyUser(userId);
            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            UserProfileDataResult result = userQueryService.getUserProfileData(userId);

            // then
            assertThat(result).isNotNull();
            then(userRepository).should(times(1)).findById(userId);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 CustomException(USER_NOT_FOUND)이 발생한다")
        void throwExceptionWhenUserNotFound() {
            // given
            Long userId = 999L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userQueryService.getUserProfileData(userId))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(UserErrorCode.USER_NOT_FOUND);
        }
    }
}
