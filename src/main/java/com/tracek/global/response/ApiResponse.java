package com.tracek.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tracek.global.response.code.BaseCode;
import com.tracek.global.response.code.BaseErrorCode;
import com.tracek.global.response.code.BaseSuccessCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final Boolean isSuccess;
    private final String code;
    private final String message;
    private final T data;

    @Builder
    private ApiResponse(Boolean isSuccess, BaseCode baseCode, T data) {
        this.isSuccess = isSuccess;
        this.code = baseCode.getCode();
        this.message = baseCode.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> success(BaseSuccessCode baseCode, T data) {
        return ApiResponse.<T>builder().isSuccess(true).baseCode(baseCode).data(data).build();
    }

    public static <T> ApiResponse<T> success(BaseSuccessCode baseCode) {
        return ApiResponse.<T>builder().isSuccess(true).baseCode(baseCode).data(null).build();
    }

    public static <T> ApiResponse<T> onFailure(BaseErrorCode baseCode, T data) {
        return ApiResponse.<T>builder().isSuccess(false).baseCode(baseCode).data(data).build();
    }

    public static <T> ApiResponse<T> onFailure(BaseErrorCode baseCode) {
        return ApiResponse.<T>builder().isSuccess(false).baseCode(baseCode).data(null).build();
    }
}
