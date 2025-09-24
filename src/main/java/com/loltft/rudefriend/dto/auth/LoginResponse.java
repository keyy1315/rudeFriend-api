package com.loltft.rudefriend.dto.auth;

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
public class LoginResponse {

  @Schema(description = "로그인 한 회원 PK")
  private UUID id;

  @Schema(description = "로그인 한 회원 이름 nullable")
  private String name;

  public static LoginResponse from(Member member) {
    return LoginResponse.builder().id(member.getId()).name(member.getName()).build();
  }
}
