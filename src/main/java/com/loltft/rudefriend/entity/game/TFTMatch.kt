package com.loltft.rudefriend.entity.game

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import org.hibernate.annotations.JdbcTypeCode
import java.sql.Types
import java.util.*

@Entity
@Table(name = "tft_match")
class TFTMatch {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "TFTMatch PK")
    var id: UUID? = null

    @JdbcTypeCode(Types.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    @Schema(description = "GameAccountInfo PK")
    var gameInfoId: UUID? = null

    @Column
    @Schema(description = "매치 ID", example = "KR_7821474749")
    var matchId: String? = null
}
