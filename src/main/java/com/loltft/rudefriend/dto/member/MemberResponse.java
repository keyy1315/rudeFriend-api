package com.loltft.rudefriend.dto.member;

import com.loltft.rudefriend.dto.game.GameInfoResponse;
import com.loltft.rudefriend.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "로그인 응답 객체")
public class MemberResponse {

  @Schema(description = "로그인 한 회원 PK")
  private UUID id;

  @Schema(description = "로그인 ID")
  private String memberId;

  @Schema(description = "로그인 한 회원 이름 nullable")
  private String name;

  private GameInfoResponse gameInfo;

  public static MemberResponse from(Member member) {
    return MemberResponse.builder()
        .id(member.getId())
        .memberId(member.getMemberId())
        .name(member.getName())
        .gameInfo(
            member.getGameAccountInfo() == null
                ? null
                : GameInfoResponse.from(member.getGameAccountInfo()))
        .build();
  }
}
