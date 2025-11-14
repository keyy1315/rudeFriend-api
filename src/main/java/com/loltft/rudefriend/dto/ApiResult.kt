package com.loltft.rudefriend.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "응답 데이터 구조")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResult<T>(
    @field:Schema(description = "실제 응답 데이터 배열 또는 객체") @param:Schema(
        description = "실제 응답 데이터 배열 또는 객체"
    ) val data: T?,
    @field:Schema(description = "목록 응답 시 전체 데이터 개수")
    val total: Long? = null
) {
    companion object {
        fun <T> of(data: T?, total: Long? = null): ApiResult<T?> = ApiResult(data, total)
    }
}
