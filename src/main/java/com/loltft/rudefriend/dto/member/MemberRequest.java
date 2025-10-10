package com.loltft.rudefriend.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.loltft.rudefriend.dto.Validation;
import com.loltft.rudefriend.dto.game.GameInfoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청 DTO")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {

  @Schema(description = "로그인 ID")
  @NotBlank(
      message = "로그인 ID는 필수값 입니다.", groups = {Validation.CREATE.class, Validation.UPDATE.class})
  private String memberId;

  @Schema(description = "비밀번호")
  @NotBlank(
      message = "비밀번호는 필수값 입니다.", groups = {Validation.CREATE.class, Validation.UPDATE.class})
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,}$", message = "비밀번호는 영문과 숫자를 포함해 8자 이상이어야 합니다.")
  private String password;

  @Schema(description = "닉네임 nullable")
  private String name;

  @Schema(description = "라이엇 게임 내 정보")
  private GameInfoRequest gameInfo;
}
