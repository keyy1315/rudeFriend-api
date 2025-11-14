package com.loltft.rudefriend.dto.board

import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.utils.ValidationGroup
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


@Schema(description = "게시글 생성 요청 DTO")
data class BoardRequest(
    @Schema(description = "게시글 제목", example = "게시글 제목")
    var title: @NotBlank(
        message = "제목은 필수값입니다.",
        groups = [ValidationGroup.CREATE::class, ValidationGroup.UPDATE::class]
    ) @Size(
        max = 100,
        message = "100자 이하로 입력하세요.",
        groups = [ValidationGroup.CREATE::class, ValidationGroup.UPDATE::class]
    ) String? = null,

    @Schema(description = "게시글 내용", example = "게시글 내용")
    var content: @NotBlank(
        message = "내용은 필수값입니다.",
        groups = [ValidationGroup.CREATE::class, ValidationGroup.UPDATE::class]
    ) @Size(
        max = 1000,
        message = "1000자 이하로 입력하세요.",
        groups = [ValidationGroup.CREATE::class, ValidationGroup.UPDATE::class]
    ) String? = null,

    @Schema(description = "게시글 태그 배열")
    var tags: MutableSet<String>? = null,

    @Schema(description = "삭제 할 파일 URL 목록")
    var shouldDeleteFileUrls: MutableList<String>? = null,

    @Schema(description = "게임 타입 (LOL/TFT)")
    var gameType: GameType = GameType.LOL,

    @Schema(description = "익명 사용자의 게시글 비밀번호")
    var password: String? = null,

    @Schema(description = "투표 시스템 사용 여부")
    var voteEnabled: Boolean = false,

    @Schema(description = "투표 항목 리스트")
    var voteItems: MutableList<String>? = null,
)
