package com.tracek.domain.artist.domain.model;

import com.tracek.global.common.vo.ImageUrl;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String alias; // 방탄소년단 -> BTS, 방탄

    @Embedded private ImageUrl pictureUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private Artist group; // 셀프 참조 (그룹/솔로 = null)

    Boolean isGroup = false;

    Long fanCount = 0L;

    Long totalVerificationCount = 0L;

    public static Artist create(
            String name, String alias, ImageUrl pictureUrl, Artist group, Boolean isGroup) {
        return new Artist(null, name, alias, pictureUrl, group, isGroup, 0L, 0L);
    }
}
