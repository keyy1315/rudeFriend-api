package com.loltft.rudefriend.jwt_security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loltft.rudefriend.dto.ApiCommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response,
      AuthenticationException e) throws IOException {
    log.info("message: {}, className: {}", e.getMessage(), e.getClass().getName());

    String errorMessage;

    switch (e) {
      case DisabledException ignored -> errorMessage = "계정이 비활성화 상태입니다.";
      case AuthenticationCredentialsNotFoundException ignored -> errorMessage = "인증 토큰이 없습니다.";
      case InsufficientAuthenticationException ignored ->
        errorMessage = "인증 토큰이 존재하지 않거나 유효하지 않습니다.";
      default -> errorMessage = e.getMessage();
    }

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    response
        .getWriter()
        .write(objectMapper.writeValueAsString(ApiCommonResponse.fail(errorMessage)));
  }
}
