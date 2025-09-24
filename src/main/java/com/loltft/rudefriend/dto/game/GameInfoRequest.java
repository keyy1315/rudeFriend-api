package com.loltft.rudefriend.dto.game;

import com.loltft.rudefriend.dto.Validation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "라이엇 계정 연동 요청 객체")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameInfoRequest {

  @Schema(description = "라이엇 계정 PUUID")
  @NotBlank(
      message = "계정 PUUID는 필수값 입니다.",
      groups = {Validation.CREATE.class, Validation.UPDATE.class})
  private UUID gameAccountId;

  @Schema(description = "계정 이름")
  @NotBlank(
      message = "계정 이름은 필수값 입니다.",
      groups = {Validation.CREATE.class, Validation.UPDATE.class})
  private String gameName;

  @Schema(description = "계정 태그")
  @NotBlank(
      message = "계정 태그는 필수값 입니다.",
      groups = {Validation.CREATE.class, Validation.UPDATE.class})
  private String tagLine;

  @Schema(description = "아이콘 URL")
  @NotBlank(
      message = "계정 아이콘 URL은 필수값 입니다.",
      groups = {Validation.CREATE.class, Validation.UPDATE.class})
  private String iconUrl;

  @Schema(description = "롤 솔랭 티어")
  private String lolTier;

  @Schema(description = "롤 자랭 티어")
  private String flexTier;

  @Schema(description = "롤체 솔랭 티어")
  private String tftTier;

  @Schema(description = "롤체 깐부 티어")
  private String doubleUpTier;
}
