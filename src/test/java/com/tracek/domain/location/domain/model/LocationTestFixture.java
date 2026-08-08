package com.tracek.domain.location.domain.model;

import com.tracek.global.common.vo.ImageUrl;
import org.springframework.test.util.ReflectionTestUtils;

public class LocationTestFixture {

    public static Location newLocation(Long id, String name, String category, Long likeCount) {
        Location location = new Location();
        ReflectionTestUtils.setField(location, "id", id);
        ReflectionTestUtils.setField(location, "name", name);
        ReflectionTestUtils.setField(location, "category", category);
        ReflectionTestUtils.setField(location, "likeCount", likeCount);
        ReflectionTestUtils.setField(location, "address", Address.of("서울특별시", "종로구", "사직로 161"));
        ReflectionTestUtils.setField(location, "geoLocation", GeoLocation.of(37.5796, 126.9770));
        ReflectionTestUtils.setField(
                location, "mainImageUrl", ImageUrl.from("http://example.com/location.jpg"));
        return location;
    }
}
