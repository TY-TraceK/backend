package com.tracek.global.exception;

import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneralException(CustomException e) {
    log.error("[Exception] {}", e.getErrorCode().getMessage());

    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(ApiResponse.onFailure(e.getErrorCode()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException e) {
    String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    log.error("[ValidationException] {}", errorMessage);

    return ResponseEntity
        .status(GeneralErrorCode.INVALID_INPUT_VALUE.getStatus())
        .body(ApiResponse.onFailure(GeneralErrorCode.INVALID_INPUT_VALUE, errorMessage));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    log.error("[UnhandledException]", e);

    return ResponseEntity
        .status(GeneralErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ApiResponse.onFailure(GeneralErrorCode.INTERNAL_SERVER_ERROR));
  }
}