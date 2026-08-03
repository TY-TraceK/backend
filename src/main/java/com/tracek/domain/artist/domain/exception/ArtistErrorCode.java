package com.tracek.domain.artist.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArtistErrorCode implements BaseErrorCode {
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTIST001", "해당 아티스트를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
