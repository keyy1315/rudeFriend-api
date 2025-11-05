package com.loltft.rudefriend.dto.board;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "게시글 목록 응답 DTO")
public class BoardListResponse {

  private Integer total;
  private List<BoardResponse> boards;
}
