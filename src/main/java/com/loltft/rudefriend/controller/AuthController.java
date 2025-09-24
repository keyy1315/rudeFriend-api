package com.loltft.rudefriend.controller;

import com.loltft.rudefriend.dto.ApiCommonResponse;
import com.loltft.rudefriend.dto.member.LoginRequest;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증/인가 API", description = "로그인 및 토큰 재발급 등 인증/인가에 관련된 기능 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "로그인",
      description =
          "사용자 로그인을 처리하고 response Header에 'access_token', 'refresh_token' 이름으로 토큰들을 저장하고"
              + " 사용자 정보를 반환합니다. ")
  @PostMapping("/login")
  public ResponseEntity<ApiCommonResponse<MemberResponse>> login(
      @RequestBody @Validated LoginRequest loginRequest, HttpServletResponse response) {
    MemberResponse result = authService.authenticateMember(loginRequest, response);
    return ResponseEntity.ok(ApiCommonResponse.success("로그인 성공", result));
  }

  @Operation(summary = "로그아웃", description = "쿠키와 DB에 저장 된 RefreshToken을 삭제합니다.")
  @PatchMapping("/logout")
  public ResponseEntity<ApiCommonResponse<Object>> logout(
      @AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
    if (userDetails == null) {
      throw new AccessDeniedException("로그인 정보가 없습니다.");
    }
    authService.logout(userDetails.getUsername(), response);
    return ResponseEntity.ok(ApiCommonResponse.success("로그아웃 성공"));
  }
}
