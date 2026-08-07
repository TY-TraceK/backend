package com.tracek.domain.user.domain.model;

import com.tracek.domain.user.domain.enums.UserRole;
import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.global.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

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

    private LocalDateTime connectAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.USER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    public User(OAuthInfo oAuthInfo, LocalDateTime connectAt, UserProfile userProfile) {
        this.oAuthInfo = oAuthInfo;
        this.connectAt = connectAt;
        this.userProfile = userProfile;
    }

    public static User createUser(
            OAuthInfo oAuthInfo, LocalDateTime connectAt, UserProfile userProfile) {
        return new User(oAuthInfo, connectAt, userProfile);
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
