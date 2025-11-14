package com.loltft.rudefriend.dto.vote

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "투표 결과 응답 DTO")
data class VoteResultResponse(
    @Schema(description = "게시글 ID")
    val boardId: UUID,

    @Schema(description = "사용자가 선택한 항목")
    val selectedItem: String,

    @Schema(description = "투표 항목별 집계(항목명 -> 득표수)")
    val voteCounts: Map<String, Long>,

    @Schema(description = "총 투표 수")
    val totalVotes: Long
)
