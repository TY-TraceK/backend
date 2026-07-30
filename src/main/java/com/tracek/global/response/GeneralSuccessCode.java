package com.tracek.global.response;

import com.tracek.global.response.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode {

  OK(HttpStatus.OK, "COMMON_001", "성공하였습니다."),
  CREATED(HttpStatus.CREATED, "COMMON_002", "생성되었습니다."),
  DELETED(HttpStatus.OK, "COMMON_003", "삭제되었습니다."),
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;
}