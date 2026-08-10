package com.tracek.domain.location.domain.model;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocationCategory {
    ATTRACTION("관광지/명소"),
    CULTURE("문화시설/박물관/미술관"),
    FESTIVAL("축제/행사"),
    FILMING_LOCATION("촬영지"),
    RESTAURANT("음식점/맛집"),
    CAFE("카페"),
    ACCOMMODATION("숙박"),
    SHOPPING("쇼핑"),
    ETC("기타");

    private final String description;

    // 문자열(String)이 들어왔을 때 대소문자 구분 없이 안전하게 Enum으로 변환
    public static LocationCategory from(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return Arrays.stream(LocationCategory.values())
                .filter(category -> category.name().equalsIgnoreCase(source.trim()))
                .findFirst()
                .orElseThrow(() -> new CustomException(LocationErrorCode.INVALID_CATEGORY));
    }
}
