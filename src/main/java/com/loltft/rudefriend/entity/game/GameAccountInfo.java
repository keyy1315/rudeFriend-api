package com.loltft.rudefriend.entity.game;

import com.loltft.rudefriend.dto.game.GameInfoRequest;
import com.loltft.rudefriend.entity.enums.Tier;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "game_account_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GameAccountInfo {

  @Id
  @JdbcTypeCode(Types.BINARY)
  @Column(columnDefinition = "BINARY(16)")
  @Schema(description = "라이엇 계정 puuid")
  private UUID id;

  @Column
  @Schema(description = "계정 이름", example = "무례한 친구")
  private String gameName;

  @Column
  @Schema(description = "계정 태그", example = "0129")
  private String tagLine;

  @Column
  @Schema(description = "아이콘 이미지 URL")
  private String iconUrl;

  @Enumerated(EnumType.STRING)
  @Column
  @Schema(description = "롤 티어", example = "PLATINUM IV")
  private Tier lolTier;

  @Enumerated(EnumType.STRING)
  @Column
  @Schema(description = "자랭 티어", example = "PLATINUM IV")
  private Tier flexTier;

  @Enumerated(EnumType.STRING)
  @Column
  @Schema(description = "롤체 티어", example = "MASTER I")
  private Tier tftTier;

  @Enumerated(EnumType.STRING)
  @Column
  @Schema(description = "깐부 티어", example = "MASTER I")
  private Tier doubleUpTier;

  public static GameAccountInfo fromRequest(GameInfoRequest gameInfo) {
    return GameAccountInfo.builder()
        .id(gameInfo.getGameAccountId())
        .gameName(gameInfo.getGameName())
        .tagLine(gameInfo.getTagLine())
        .iconUrl(gameInfo.getIconUrl())
        .lolTier(gameInfo.getLolTier())
        .flexTier(gameInfo.getFlexTier())
        .tftTier(gameInfo.getTftTier())
        .doubleUpTier(gameInfo.getDoubleUpTier())
        .build();
  }
}
