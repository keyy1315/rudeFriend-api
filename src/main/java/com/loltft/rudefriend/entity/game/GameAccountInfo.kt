package com.loltft.rudefriend.entity.game

import com.loltft.rudefriend.dto.game.GameInfoRequest
import com.loltft.rudefriend.entity.enums.Tier
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types
import java.util.*

@Entity
@Table(name = "game_account_info")
class GameAccountInfo(
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "라이엇 계정 puuid")
    private var id: UUID? = null,

    @Column
    @Schema(description = "계정 이름", example = "무례한 친구")
    private var gameName: String? = null,

    @Column
    @Schema(description = "계정 태그", example = "0129")
    private var tagLine: String? = null,

    @Column
    @Schema(description = "아이콘 이미지 URL")
    private var iconUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column
    @Schema(description = "롤 티어", example = "PLATINUM")
    private var lolTier: Tier? = null,

    @Enumerated(EnumType.STRING)
    @Column
    @Schema(description = "자랭 티어", example = "PLATINUM")
    private var flexTier: Tier? = null,

    @Enumerated(EnumType.STRING)
    @Column
    @Schema(description = "롤체 티어", example = "MASTER")
    private var tftTier: Tier? = null,

    @Enumerated(EnumType.STRING)
    @Column
    @Schema(description = "깐부 티어", example = "MASTER")
    private var doubleUpTier: Tier? = null
) {

    companion object {
        fun fromRequest(gameInfoRequest: GameInfoRequest): GameAccountInfo {
            return GameAccountInfo().apply {
                gameName = gameInfoRequest.gameName
                tagLine = gameInfoRequest.tagLine
                iconUrl = gameInfoRequest.iconUrl
                lolTier = gameInfoRequest.lolTier
                flexTier = gameInfoRequest.flexTier
                doubleUpTier = gameInfoRequest.doubleUpTier
                tftTier = gameInfoRequest.tftTier
                id = gameInfoRequest.gameAccountId
            }
        }

        fun from(gameInfo: GameAccountInfo): GameAccountInfo {
            return GameAccountInfo().apply {
                gameName = gameInfo.gameName
                tagLine = gameInfo.tagLine
                iconUrl = gameInfo.iconUrl
                lolTier = gameInfo.lolTier
                flexTier = gameInfo.flexTier
                doubleUpTier = gameInfo.doubleUpTier
                tftTier = gameInfo.tftTier
                id = gameInfo.id
            }
        }
    }
}
