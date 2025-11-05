package com.loltft.rudefriend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "응답 데이터 구조")
public record ApiResult<T>(@Schema(description = "실제 응답 데이터 배열 또는 객체") T data) {

  public static <T> ApiResult<T> of(T data) {
    return ApiResult.<T>builder().data(data).build();
  }
}
