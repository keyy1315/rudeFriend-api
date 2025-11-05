package com.loltft.rudefriend.dto.board

import com.loltft.rudefriend.dto.enums.GameType
import io.swagger.v3.oas.annotations.media.Schema
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import java.util.*

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시글 응답 DTO")
data class BoardResponse(
    @Schema(description = "게시글 PK")
    var id: UUID? = null,

    @Schema(description = "게시글 제목")
    var title: String? = null,

    @Schema(description = "게시글 내용")
    var content: String? = null,

    @Schema(description = "게시글 태그")
    var tags: MutableSet<String?>? = null,

    @Schema(description = "게시글 게임 타입")
    var gameType: GameType? = null
)
