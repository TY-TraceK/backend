package com.tracek.domain.content.domain.model;

import com.tracek.domain.content.domain.exception.ContentErrorCode;
import com.tracek.global.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentCategory {
    KPOP("K-POP"),
    DRAMA("드라마"),
    MOVIE("영화"),
    ENTERTAINMENT("예능"),
    WEBTOON("웹툰/애니메이션"),
    ETC("기타");

    private final String description;

    // 문자열(String)이 들어왔을 때 대소문자 구분 없이 안전하게 Enum으로 변환
    public static ContentCategory from(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return Arrays.stream(ContentCategory.values())
                .filter(category -> category.name().equalsIgnoreCase(source.trim()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ContentErrorCode.INVALID_CATEGORY));
    }
}
