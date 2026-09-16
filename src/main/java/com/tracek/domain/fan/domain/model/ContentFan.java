package com.tracek.domain.fan.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content_fan")
@IdClass(ContentFanId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentFan {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    private ContentFan(Long userId, Long contentId) {
        this.userId = userId;
        this.contentId = contentId;
    }

    public static ContentFan create(ContentFanId id) {
        return new ContentFan(id.userId(), id.contentId());
    }
}
