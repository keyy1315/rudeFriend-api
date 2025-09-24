package com.loltft.rudefriend.exception;

import com.loltft.rudefriend.dto.ApiCommonResponse;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiCommonResponse<String>> handleException(Exception e) {
    log.error("Exception 오류 status - 500 : {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiCommonResponse.failure("서버 내부 오류가 발생했습니다."));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiCommonResponse<String>> handleIllegalStateException(
      IllegalStateException e) {
    log.error("IllegalStateException 오류 status - 500 : {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiCommonResponse.failure(e.getMessage()));
  }

  /// ============================ 인증 인가 에러 ============================

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ApiCommonResponse<String>> handleUsernameNotFoundException(
      UsernameNotFoundException e) {
    log.error("UsernameNotFoundException 오류 status - 404 ID : {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiCommonResponse.failure("회원 정보를 찾을 수 없습니다 ID : " + e.getMessage()));
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ApiCommonResponse<String>> handleJwtException(JwtException e) {
    log.error("JwtException 오류 status - 401 : {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiCommonResponse.failure(e.getMessage()));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiCommonResponse<Object>> handleAuthenticationException(
      AuthenticationException e) {
    log.error("AuthenticationException 오류 status - 401 : {}", e.getMessage());
    String errorMessage;
    switch (e) {
      case BadCredentialsException ignored -> errorMessage = "아이디 또는 비밀번호가 틀렸습니다.";
      case DisabledException ignored -> errorMessage = "계정이 비활성화되어 있습니다.";
      default -> errorMessage = e.getMessage();
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiCommonResponse.failure(errorMessage));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiCommonResponse<Object>> handleAccessDeniedException(
      AccessDeniedException e) {
    log.error("AccessDeniedException 오류 status - 401 : {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiCommonResponse.failure(e.getMessage()));
  }
}
