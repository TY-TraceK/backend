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

    @Embedded private Address address;

    @Embedded private GeoLocation geoLocation;

    @Embedded private ImageUrl mainImageUrl; // 목록/지도 핀용 대표 이미지 VO

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageLocation> imageLocations = new ArrayList<>();

    @Column(length = 20)
    private String tel;

    @Column(length = 100)
    private String businessHours;

    @Column(length = 500)
    private String overview;

    private Long externalContentId; // 데이터 배치 시 중복 삽입 방지

    @Column(length = 20)
    private String sourceType; // 데이터 배치 출처 ("TOUR_API", "CURATED")

    private Long likeCount = 0L;

    private Long archiveCount = 0L;

    private Long totalVerificationCount = 0L;

    public void increaseLikeCount() {
        this.likeCount = (this.likeCount == null ? 0L : this.likeCount) + 1;
    }

    public void decreaseLikeCount() {
        long current = this.likeCount == null ? 0L : this.likeCount;
        this.likeCount = current > 0 ? current - 1 : 0L;
    }
}
