package com.tracek.domain.user.infrastructure.persistence;

import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import com.tracek.domain.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByOAuthInfo(OAuthInfo oAuthInfo) {
        return userJpaRepository.findByoAuthInfo(oAuthInfo);
    }

    @Override
    public boolean findByIdAndUserStatusIs(Long userId, UserStatus userStatus) {
        return userJpaRepository.existsByIdAndUserStatusIs(userId, userStatus);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
