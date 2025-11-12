package com.loltft.rudefriend.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "API 공통 응답 구조")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiCommonResponse<T>(
    @Schema(description = "응답 상태")
    val status: String? = null,

    @Schema(description = "응답 메세지", example = "상세 데이터 조회 성공")
    val message: String? = null,

    @Schema(description = "응답 데이터")
    val result: ApiResult<T>? = null
) {
    companion object {
        private const val OK = "ok"
        private const val FAIL = "fail"

        /**
         * 단일 성공 응답
         *
         * @param message 응답 메세지
         * @param data    응답 데이터
         * @return 단일 데이터만 존재하는 공통 응답 객체
         */
        fun <T> ok(message: String?, data: T?, total: Long? = null): ApiCommonResponse<T?>? {
            return ApiCommonResponse(
                status = OK,
                message = message,
                result = ApiResult.of(data, total)
            )
        }

        /**
         * 성공 응답
         *
         * @param message 응답 메세지
         * @return 메세지만 존재하는 공통 응답 객체
         */
        @JvmStatic
        fun <T> ok(message: String?): ApiCommonResponse<T>? {
            return ApiCommonResponse(
                status = OK,
                message = message,
            )
        }

        /**
         * 실패 응답
         *
         * @param message 응답 메세지
         * @return 메세지만 존재하는 공통 응답 객체
         */
        @JvmStatic
        fun <T> fail(message: String?): ApiCommonResponse<T>? {
            return ApiCommonResponse(
                status = FAIL,
                message = message,
            )
        }
    }
}
