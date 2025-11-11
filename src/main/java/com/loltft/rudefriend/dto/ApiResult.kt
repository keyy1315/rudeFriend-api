package com.loltft.rudefriend.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "응답 데이터 구조")
data class ApiResult<T>(
    @field:Schema(description = "실제 응답 데이터 배열 또는 객체") @param:Schema(
        description = "실제 응답 데이터 배열 또는 객체"
    ) val data: T?
) {
    companion object {
        fun <T> of(data: T?): ApiResult<T?> = ApiResult(data)
    }
}
