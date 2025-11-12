package com.loltft.rudefriend.dto.board

import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.dto.member.MemberResponse
import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.game.GameAccountInfo
import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "게시글 응답 DTO")
data class BoardResponse(
    @Schema(description = "게시글 PK")
    var id: UUID? = null,

    @Schema(description = "게시글 제목")
    var title: String? = null,

    @Schema(description = "게시글 내용")
    var content: String? = null,

    @Schema(description = "게시글 태그")
    var tags: MutableSet<String>? = null,

    @Schema(description = "게시글 게임 타입")
    var gameType: GameType? = null,

    @Schema(description = "게시글 이미지 URL 목록")
    var fileUrls: MutableList<String>? = null,
) {
    companion object {
        @JvmStatic
        fun of(board: Board): BoardResponse {
            return BoardResponse().apply {
                id = board.id
                title = board.title
                content = board.content
                tags = board.tags
                gameType = board.gameType
                fileUrls = board.fileUrls
            }
        }
    }
}
