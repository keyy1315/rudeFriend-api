package com.loltft.rudefriend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "API 공통 응답 구조")
public class ApiCommonResponse<T> {

  @Schema(description = "응답 상태")
  private final String status;

  @Schema(description = "응답 메세지", example = "상세 데이터 조회 성공")
  private final String message;

  @Schema(description = "응답 데이터")
  private final ApiResult<T> result;

  public ApiCommonResponse() {
    this.status = null;
    this.message = null;
    this.result = null;
  }

  private static final String OK = "ok";
  private static final String FAIL = "fail";

  /**
   * 목록 성공 응답
   *
   * @param message 응답 메세지
   * @param data    응답 데이터
   * @param count   데이터 개수
   * @return 조회된 데이터 개수, 응답 데이터 목록이 포함 된 공통 응답 객체
   */
  public static <T> ApiCommonResponse<T> ok(String message, T data, Integer count) {
    return ApiCommonResponse.<T>builder()
        .status(OK)
        .message(message)
        .result(ApiResult.of(count, data))
        .build();
  }

  /**
   * 단일 성공 응답
   *
   * @param message 응답 메세지
   * @param data    응답 데이터
   * @return 단일 데이터만 존재하는 공통 응답 객체
   */
  public static <T> ApiCommonResponse<T> ok(String message, T data) {
    return ApiCommonResponse.<T>builder()
        .status(OK)
        .message(message)
        .result(ApiResult.of(data))
        .build();
  }

  /**
   * 성공 응답
   *
   * @param message 응답 메세지
   * @return 메세지만 존재하는 공통 응답 객체
   */
  public static <T> ApiCommonResponse<T> ok(String message) {
    return ApiCommonResponse.<T>builder().status(OK).message(message).build();
  }

  /**
   * 실패 응답
   *
   * @param message 응답 메세지
   * @return 메세지만 존재하는 공통 응답 객체
   */
  public static <T> ApiCommonResponse<T> fail(String message) {
    return ApiCommonResponse.<T>builder().status(FAIL).message(message).build();
  }
}
