package com.loltft.rudefriend.dto.game;

import com.loltft.rudefriend.entity.game.GameAccountInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "게임 정보 응답 객체")
public class GameInfoResponse {

  @Schema(description = "계정 PUUID")
  private UUID id;

  @Schema(description = "계정 이름")
  private String gameName;

  @Schema(description = "계정 태그")
  private String tagLine;

  @Schema(description = "아이콘 URL")
  private String iconUrl;

  @Schema(description = "롤 솔랭 티어")
  private String lolTier;

  @Schema(description = "롤 자랭 티어")
  private String flexTier;

  @Schema(description = "롤체 솔랭 티어")
  private String tftTier;

  @Schema(description = "롤체 깐부 티어")
  private String doubleUpTier;

  public static GameInfoResponse from(GameAccountInfo gameAccountInfo) {
    return GameInfoResponse.builder()
        .id(gameAccountInfo.getId())
        .gameName(gameAccountInfo.getGameName())
        .tagLine(gameAccountInfo.getTagLine())
        .iconUrl(gameAccountInfo.getIconUrl())
        .lolTier(gameAccountInfo.getLolTier())
        .flexTier(gameAccountInfo.getFlexTier())
        .tftTier(gameAccountInfo.getTftTier())
        .doubleUpTier(gameAccountInfo.getDoubleUpTier())
        .build();
  }
}
