package com.loltft.rudefriend.dto.board

import io.swagger.v3.oas.annotations.media.Schema


@Schema(description = "게시글 목록 응답 DTO")
data class BoardListResponse(
    var total: Int? = null,
    var boards: MutableList<BoardResponse?>? = null
)

