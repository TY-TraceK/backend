package com.tracek.domain.user.domain.model;

import com.tracek.domain.user.domain.enums.UserRole;
import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded private OAuthInfo oAuthInfo;

    @Setter @Embedded private UserProfile userProfile;

    private OffsetDateTime connectedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.USER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    public User(OAuthInfo oAuthInfo, OffsetDateTime connectAt, UserProfile userProfile) {
        this.oAuthInfo = oAuthInfo;
        this.connectedAt = connectAt;
        this.userProfile = userProfile;
    }

    public static User createUser(
            OAuthInfo oAuthInfo, OffsetDateTime connectedAt, UserProfile userProfile) {
        return new User(oAuthInfo, connectedAt, userProfile);
    }

    public void updateConnectedAt(OffsetDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }

    public void grantAdminRole() {
        this.userRole = UserRole.ADMIN;
    }

    public void suspend() {
        this.userStatus = UserStatus.SUSPENDED;
    }

    public void withdraw() {
        this.userStatus = UserStatus.WITHDRAWN;
        softDelete(id);
    }
}
