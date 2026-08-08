package com.tracek.domain.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.user.domain.enums.OAuthProvider;
import com.tracek.domain.user.domain.enums.UserRole;
import com.tracek.domain.user.domain.enums.UserStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

    private OAuthInfo oAuthInfo;
    private UserProfile userProfile;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        oAuthInfo = OAuthInfo.register(12345L, OAuthProvider.KAKAO); // OAuthProvider 종류에 맞춰 사용하세요

        userProfile = UserProfile.register("테스트닉네임", "https://example.com/profile.jpg");

        now = LocalDateTime.now();
    }

    @Nested
    @DisplayName("유저 생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("createUser 정적 팩토리 메서드로 유저를 생성하면 기본값(USER, ACTIVE)이 올바르게 설정된다.")
        void createUser_Success() {
            // when
            User user = User.createUser(oAuthInfo, now, userProfile);

            // then
            assertThat(user).isNotNull();
            assertThat(user.getOAuthInfo()).isEqualTo(oAuthInfo);
            assertThat(user.getUserProfile()).isEqualTo(userProfile);
            assertThat(user.getConnectAt()).isEqualTo(now);

            // Enum 기본값 검증
            assertThat(user.getUserRole()).isEqualTo(UserRole.USER);
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("도메인 비즈니스 로직 테스트")
    class DomainLogicTest {

        private User user;

        @BeforeEach
        void setUp() {
            user = User.createUser(oAuthInfo, now, userProfile);
        }

        @Test
        @DisplayName("grantAdminRole 호출 시 유저의 권한이 ADMIN으로 변경된다.")
        void grantAdminRole() {
            // when
            user.grantAdminRole();

            // then
            assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
        }

        @Test
        @DisplayName("suspend 호출 시 유저의 상태가 SUSPENDED로 변경된다.")
        void suspend() {
            // when
            user.suspend();

            // then
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
        }

        @Test
        @DisplayName("withdraw 호출 시 유저의 상태가 WITHDRAWN으로 변경된다.")
        void withdraw() {
            // when
            user.withdraw();

            // then
            assertThat(user.getUserStatus()).isEqualTo(UserStatus.WITHDRAWN);
        }
    }

    @Nested
    @DisplayName("프로필 수정 테스트")
    class ProfileTest {

        @Test
        @DisplayName("setUserProfile을 통해 유저의 프로필을 새로운 프로필로 변경할 수 있다.")
        void setUserProfile() {
            // given
            User user = User.createUser(oAuthInfo, now, userProfile);
            new UserProfile();
            UserProfile newProfile = UserProfile.register("새닉네임", "https://example.com/new.jpg");

            // when
            user.setUserProfile(newProfile);

            // then
            assertThat(user.getUserProfile()).isEqualTo(newProfile);
        }
    }
}
