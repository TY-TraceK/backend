package com.tracek.global.response;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

  BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "적절하지 않은 입력값입니다."),
  MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_003", "필수 요청 파라미터가 누락되었습니다."),

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_004", "인증이 필요합니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_005", "접근 권한이 없습니다."),

  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_006", "요청한 리소스를 찾을 수 없습니다."),

  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_007", "지원하지 않는 HTTP 메서드입니다."),

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_008", "서버 내부 에러가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}