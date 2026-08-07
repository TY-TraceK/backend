package com.tracek.domain.user.domain.repository;

import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import java.util.Optional;

public interface UserRepository {

  Optional<User> findByOAuthInfo(OAuthInfo oAuthInfo);

  boolean findByIdAndUserStatusIs(Long userId, UserStatus userStatus);

  Optional<User> findById(Long userId);

  User save(User user);
}
