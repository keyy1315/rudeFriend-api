package com.loltft.rudefriend.dto.board;

import java.util.Set;
import java.util.UUID;

import com.loltft.rudefriend.dto.enums.GameType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시글 응답 DTO")
public class BoardResponse {

  @Schema(description = "게시글 PK")
  private UUID id;
  @Schema(description = "게시글 제목")
  private String title;
  @Schema(description = "게시글 내용")
  private String content;
  @Schema(description = "게시글 태그")
  private Set<String> tags;
  @Schema(description = "게시글 게임 타입")
  private GameType gameType;
}
