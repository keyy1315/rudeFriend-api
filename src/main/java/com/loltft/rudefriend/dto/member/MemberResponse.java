package com.loltft.rudefriend.dto.member;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loltft.rudefriend.entity.Member;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.game.GameAccountInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "회원 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberResponse {

  @Schema(description = "로그인 한 회원 PK")
  private UUID id;

  @Schema(description = "로그인 ID")
  private String memberId;

  @Schema(description = "로그인 한 회원 이름 nullable")
  private String name;

  @Schema(description = "회원 상태")
  private Boolean status;

  @Schema(description = "회원 권한")
  private Role role;

  @Schema(description = "계정 생성 일시")
  private LocalDateTime createdAt;

  @Schema(description = "계정 수정 일시")
  private LocalDateTime updatedAt;

  private GameAccountInfo gameInfo;

  public static MemberResponse from(Member member) {
    return MemberResponse.builder()
        .id(member.getId())
        .memberId(member.getMemberId())
        .name(member.getName())
        .gameInfo(
            member.getGameAccountInfo() == null
                ? null
                : GameAccountInfo.fromMember(member.getGameAccountInfo()))
        .status(member.getStatus())
        .role(member.getRole())
        .build();
  }
}
