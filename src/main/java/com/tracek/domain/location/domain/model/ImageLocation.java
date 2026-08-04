package com.tracek.domain.location.domain.model;

import com.tracek.domain.image.domain.model.Image;
import com.tracek.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "image_location",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UK_image_location_location_image",
                    columnNames = {"location_id", "image_id"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_main", nullable = false)
    private Boolean isMain;

    public static ImageLocation create(
            Location location, Image image, Integer displayOrder, Boolean isMain) {
        ImageLocation imageLocation =
                new ImageLocation(
                        null,
                        location,
                        image,
                        displayOrder != null ? displayOrder : 1,
                        isMain != null ? isMain : false);

        // Location의 연관관계 리스트에 자동 추가
        if (location != null && !location.getImageLocations().contains(imageLocation)) {
            location.getImageLocations().add(imageLocation);
        }

        return imageLocation;
    }

    // 비즈니스 메서드 (대표 사진 변경/취소, 순서 변경)
    public void updateDisplayOrder(int newOrder) {
        this.displayOrder = newOrder;
    }

    public void setAsMain() {
        this.isMain = true;
    }

    public void unsetMain() {
        this.isMain = false;
    }
}
