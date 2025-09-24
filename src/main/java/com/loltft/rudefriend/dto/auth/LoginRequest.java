package com.loltft.rudefriend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 요청 객체")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @Schema(description = "로그인 ID", example = "super")
  @NotBlank(message = "로그인 ID는 필수값 입니다.")
  private String memberId;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Schema(description = "로그인 비밀번호", example = "1234")
  @NotBlank(message = "비밀번호는 필수값 입니다.")
  private String password;
}
