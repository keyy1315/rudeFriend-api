package com.loltft.rudefriend.dto.vote

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "투표 요청 DTO")
data class VoteRequest(
    @field:NotBlank(message = "투표 항목은 필수입니다.")
    @Schema(description = "선택한 투표 항목", example = "선택지 A")
    val voteItem: String = ""
)
