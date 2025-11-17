package com.loltft.rudefriend.dto.game

import com.loltft.rudefriend.dto.Validation
import com.loltft.rudefriend.entity.enums.Tier
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.util.*

@Schema(description = "라이엇 계정 연동 요청 DTO")
data class GameInfoRequest(
    @Schema(description = "라이엇 계정 PUUID")
    var gameAccountId: @NotBlank(
        message = "계정 PUUID는 필수값 입니다.",
        groups = [Validation.CREATE::class, Validation.UPDATE::class]
    ) UUID? = null,

    @Schema(description = "계정 이름")
    var gameName: @NotBlank(
        message = "계정 이름은 필수값 입니다.",
        groups = [Validation.CREATE::class, Validation.UPDATE::class]
    ) String? = null,

    @Schema(description = "계정 태그")
    var tagLine: @NotBlank(
        message = "계정 태그는 필수값 입니다.",
        groups = [Validation.CREATE::class, Validation.UPDATE::class]
    ) String? = null,

    @Schema(description = "아이콘 URL")
    var iconUrl: @NotBlank(
        message = "계정 아이콘 URL은 필수값 입니다.",
        groups = [Validation.CREATE::class, Validation.UPDATE::class]
    ) String? = null,

    @Schema(description = "롤 솔랭 티어")
    var lolTier: Tier? = null,

    @Schema(description = "롤 자랭 티어")
    var flexTier: Tier? = null,

    @Schema(description = "롤체 솔랭 티어")
    var tftTier: Tier? = null,

    @Schema(description = "롤체 깐부 티어")
    var doubleUpTier: Tier? = null
)
