package com.loltft.rudefriend.controller;

import com.loltft.rudefriend.dto.ApiCommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증/인가 API", description = "로그인 및 토큰 재발급 등 인증/인가에 관련된 기능 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

  @PostMapping("/login")
  public ResponseEntity<ApiCommonResponse<String>> login() {
    return null;
  }
}
