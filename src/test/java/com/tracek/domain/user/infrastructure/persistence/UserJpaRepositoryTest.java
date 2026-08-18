package com.tracek.domain.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.user.domain.enums.OAuthProvider;
import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import com.tracek.domain.user.domain.model.UserProfile;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserJpaRepositoryTest {

    @Autowired private UserJpaRepository userJpaRepository;

    @Autowired private TestEntityManager entityManager;

    private OAuthInfo oAuthInfo;
    private User testUser;

    @BeforeEach
    void setUp() {
        oAuthInfo = OAuthInfo.register(12345L, OAuthProvider.KAKAO);
        UserProfile userProfile = UserProfile.register("테스트닉네임", "https://example.com/profile.jpg");
        OffsetDateTime now = OffsetDateTime.now();

        testUser = User.createUser(oAuthInfo, now, userProfile);
    }

    @Nested
    @DisplayName("findByoAuthInfo() 테스트")
    class FindByOAuthInfoTest {

        @Test
        @DisplayName("일치하는 OAuthInfo 정보가 존재하면 유저 정보를 반환한다.")
        void findByoAuthInfo_success() {
            // given
            User savedUser = userJpaRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<User> result = userJpaRepository.findByoAuthInfo(oAuthInfo);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(savedUser.getId());
        }

        @Test
        @DisplayName("존재하지 않는 OAuthInfo 정보로 조회 시 빈 Optional을 반환한다.")
        void findByoAuthInfo_notFound() {
            // given
            userJpaRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            OAuthInfo nonExistentInfo = OAuthInfo.register(99999L, OAuthProvider.KAKAO);

            // when
            Optional<User> result = userJpaRepository.findByoAuthInfo(nonExistentInfo);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existByIdAndUserStatusIs() 테스트")
    class ExistByIdAndUserStatusIsTest {

        @Test
        @DisplayName("유저 ID와 상태값이 모두 일치하면 true를 반환한다.")
        void existByIdAndUserStatusIs_returnsTrue() {
            // given
            User savedUser = userJpaRepository.save(testUser);
            entityManager.flush();
            entityManager.clear();

            // when
            boolean exists =
                    userJpaRepository.existsByIdAndUserStatusIs(
                            savedUser.getId(), UserStatus.ACTIVE);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("유저 ID는 일치하지만 상태값이 일치하지 않으면 false를 반환한다.")
        void existByIdAndUserStatusIs_statusMismatch_returnsFalse() {
            // given
            User savedUser = userJpaRepository.save(testUser); // ACTIVE 상태로 저장
            entityManager.flush();
            entityManager.clear();

            // when (다른 상태값으로 조회)
            boolean exists =
                    userJpaRepository.existsByIdAndUserStatusIs(
                            savedUser.getId(), UserStatus.SUSPENDED);

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 유저 ID로 조회 시 false를 반환한다.")
        void existByIdAndUserStatusIs_nonExistentId_returnsFalse() {
            // when
            boolean exists = userJpaRepository.existsByIdAndUserStatusIs(999L, UserStatus.ACTIVE);

            // then
            assertThat(exists).isFalse();
        }
    }
}
