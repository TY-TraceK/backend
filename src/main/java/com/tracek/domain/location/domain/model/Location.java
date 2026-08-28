package com.tracek.domain.location.domain.model;

import com.tracek.global.common.BaseEntity;
import com.tracek.global.common.vo.ImageUrl;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "location",
        indexes = {
            @Index(
                    name = "idx_location_category",
                    columnList = "category, id DESC") // category 조건 필터링 + id 내림차순 정렬/커서 최적화
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LocationCategory category;

    private Long likeCount = 0L;

    @Embedded private Address address;

    @Embedded private GeoLocation geoLocation;

    @Embedded private ImageUrl mainImageUrl; // 목록/지도 핀용 대표 이미지 VO

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageLocation> imageLocations = new ArrayList<>();

    public void increaseLikeCount() {
        this.likeCount = (this.likeCount == null ? 0L : this.likeCount) + 1;
    }

    public void decreaseLikeCount() {
        long current = this.likeCount == null ? 0L : this.likeCount;
        this.likeCount = current > 0 ? current - 1 : 0L;
    }
}
