package com.tracek.domain.user.domain.model;

import com.tracek.domain.user.domain.enums.UserRole;
import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id @GeneratedValue private Long id;

    private Email email;
    private String nickName;
    private UserRole userRole;
    private UserStatus userStatus;

    private User(Email email, String nickName, UserRole userRole, UserStatus userStatus) {
        this.email = email;
        this.nickName = nickName;
        this.userRole = userRole;
        this.userStatus = userStatus;
    }

    public static User createUser(Email email, String nickName) {
        return new User(email, nickName, UserRole.USER, UserStatus.ACTIVE);
    }

    public void suspend() {
        this.userStatus = UserStatus.SUSPENDED;
    }

    public void withdraw() {
        this.userStatus = UserStatus.WITHDRAWN;
        softDelete(id);
    }
}
