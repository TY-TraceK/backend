package com.tracek.domain.user.infrastructure.persistence;

import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByoAuthInfo(OAuthInfo oAuthInfo);

    boolean existsByIdAndUserStatusIs(Long id, UserStatus userStatus);
}
