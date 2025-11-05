package com.loltft.rudefriend.dto.board;

import java.util.Set;

import com.loltft.rudefriend.dto.enums.GameType;
import com.loltft.rudefriend.utils.ValidationGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "게시글 생성 요청 DTO")
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequest {

  @Schema(description = "게시글 제목", example = "게시글 제목")
  @NotBlank(message = "제목은 필수값입니다.", groups = {ValidationGroup.CREATE.class,
      ValidationGroup.UPDATE.class})
  @Size(max = 100, message = "100자 이하로 입력하세요.", groups = {ValidationGroup.CREATE.class,
      ValidationGroup.UPDATE.class})
  private String title;

  @Schema(description = "게시글 내용", example = "게시글 내용")
  @NotBlank(message = "내용은 필수값입니다.", groups = {ValidationGroup.CREATE.class,
      ValidationGroup.UPDATE.class})
  @Size(max = 1000, message = "1000자 이하로 입력하세요.", groups = {ValidationGroup.CREATE.class,
      ValidationGroup.UPDATE.class})
  private String content;

  @Schema(description = "게시글 태그 배열")
  private Set<String> tags;

  @Schema(description = "게임 타입 (LOL/TFT)")
  private GameType gameType = GameType.LOL;
}
