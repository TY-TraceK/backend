package com.tracek.domain.content.domain.model;

import com.tracek.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Episode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(length = 500)
    private String sourceUrl; // 출처 주소

    @Column(length = 200)
    private String episodeInfo;

    @Column(length = 20)
    private String visitDate;

    @Column(length = 500)
    private String note;

    public static Episode create(
            Content content, String sourceUrl, String episodeInfo, String visitDate, String note) {
        return new Episode(null, content, sourceUrl, episodeInfo, visitDate, note);
    }
}
