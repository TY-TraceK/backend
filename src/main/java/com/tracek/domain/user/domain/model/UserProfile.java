package com.tracek.domain.user.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    public static UserProfile register(String nickname, String profileImageUrl) {
        return new UserProfile(nickname, profileImageUrl);
    }
}
