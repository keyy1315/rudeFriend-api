package com.loltft.rudefriend.dto.board

import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.entity.Board
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

    @Schema(description = "작성자 Ip/memberId")
    var createdBy: String? = null,
) {
    companion object {
        /**
         * 게시글 엔티티를 응답 DTO로 변환한다.
         *
         * @param board         게시글 엔티티
         * @param fullFileUrls  첨부 파일 전체 URL 목록
         * @return 변환된 게시글 응답 DTO
         */
        @JvmStatic
        fun of(board: Board, fullFileUrls: MutableList<String>?): BoardResponse {
            return BoardResponse().apply {
                id = board.id
                title = board.title
                content = board.content
                tags = board.tags
                gameType = board.gameType
                createdBy = board.createdBy
                fileUrls = fullFileUrls
            }
        }
    }
}
