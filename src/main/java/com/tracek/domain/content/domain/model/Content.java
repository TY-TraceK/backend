package com.tracek.domain.content.domain.model;

import com.tracek.global.common.BaseEntity;
import com.tracek.global.common.vo.ImageUrl;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "content",
        indexes = {
            @Index(
                    name = "idx_content_category",
                    columnList = "category, id DESC") // category 조건 필터링 + id 내림차순 정렬/커서 최적화
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    private ContentCategory category;

    @Embedded private ImageUrl pictureUrl;

    public static Content create(String title, String category, ImageUrl pictureUrl) {
        return new Content(null, title, ContentCategory.from(category), pictureUrl);
    }
}
