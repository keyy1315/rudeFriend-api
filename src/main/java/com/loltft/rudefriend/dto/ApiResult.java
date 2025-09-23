package com.loltft.rudefriend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "응답 데이터 구조")
public class ApiResult<T> {

  @Schema(description = "데이터 개수", example = "2")
  private final int count;

  @Schema(description = "실제 응답 데이터 배열 또는 객체")
  private final T data;

  public ApiResult() {
    this.count = 0;
    this.data = null;
  }

  public static <T> ApiResult<T> of(int count, T data) {
    return ApiResult.<T>builder().count(count).data(data).build();
  }

  public static <T> ApiResult<T> of(T data) {
    return ApiResult.<T>builder().data(data).build();
  }
}
