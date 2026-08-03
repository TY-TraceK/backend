package com.tracek.domain.image.domain.model;

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
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded private ImageUrl imageUrl;

    public static Image create(String url) {
        return new Image(null, ImageUrl.from(url));
    }
}
