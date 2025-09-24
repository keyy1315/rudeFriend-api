package com.loltft.rudefriend.jwt_security;

import com.loltft.rudefriend.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final String BEARER = "Bearer ";
  private static final String AUTHORIZATION = "Authorization";

  private final JwtProperties jwtProperties;

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 토큰 검증 Exception 의 에러 메세지 설정을 위한 메소드
   *
   * @param e Exception
   * @return errorMessage
   */
  public String handleJwtExceptionMessage(Exception e) {
    String errorMessage;
    switch (e) {
      case ExpiredJwtException ignored -> errorMessage = "토큰이 만료되었습니다.";
      case MalformedJwtException ignored -> errorMessage = "토큰이 잘못되었거나 변조되었습니다.";
      case SignatureException ignored -> errorMessage = "서명이 맞지 않습니다.";
      case UnsupportedJwtException ignored -> errorMessage = "해당 토큰의 포맷은 지원하지 않습니다.";
      case IllegalArgumentException ignored -> errorMessage = "토큰의 클레임을 읽을 수 없습니다.";
      default -> errorMessage = e.getMessage();
    }
    return errorMessage;
  }

  /**
   * AccessToken 생성
   *
   * @param authentication 인증 객체
   * @return 생성 된 AccessToken
   */
  public String generateAccessToken(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Date now = new Date();
    Date expireDate = new Date(now.getTime() + jwtProperties.getExpiration());

    return Jwts.builder()
        .claim(jwtProperties.getClaimKey(), userDetails.getUsername())
        .subject(jwtProperties.getAccessTokenSubject())
        .issuedAt(now)
        .expiration(expireDate)
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * accessToken 재발급을 위한 refreshToken 생성
   *
   * @return 생성 된 refreshToken
   */
  public String generateRefreshToken() {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

    return Jwts.builder()
        .subject(jwtProperties.getRefreshTokenSubject())
        .issuedAt(now)
        .expiration(expireDate)
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * @param token 토큰
   * @return 토큰에서 추출한 관리자 ID
   * @throws AuthenticationCredentialsNotFoundException args에 토큰이 빈 값일 때
   */
  public String getUsernameFromAccessToken(String token) {
    if (!StringUtils.hasText(token)) {
      throw new AuthenticationCredentialsNotFoundException("토큰 정보가 없습니다.");
    }
    try {
      Claims claims =
          Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
      return claims.get(jwtProperties.getClaimKey()).toString();
    } catch (Exception e) {
      throw new JwtException(handleJwtExceptionMessage(e));
    }
  }

  /**
   * @param token 토큰
   * @return 토큰의 subject - access / refresh
   * @throws AuthenticationCredentialsNotFoundException args에 토큰이 빈 값일 때
   */
  public String getTokenSubject(String token) {
    if (!StringUtils.hasText(token)) {
      throw new AuthenticationCredentialsNotFoundException("토큰 정보가 없습니다.");
    }
    try {
      Claims claims =
          Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
      return claims.getSubject();
    } catch (Exception e) {
      throw new JwtException(handleJwtExceptionMessage(e));
    }
  }

  /**
   * @param request HttpServletRequest
   * @return token
   */
  public String getAccessTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
      return bearerToken.substring(BEARER.length());
    }
    return null;
  }

  /**
   * 토큰 유효성 검증
   *
   * @param token 검증 할 토큰
   * @return 토큰 유효성 (true, false)
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (JwtException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("JWT 파싱 오류");
    }
  }

  /**
   * 요청 쿠키에서 refreshToken 값을 추출
   *
   * @param request 요청
   * @return refreshToken
   */
  public String getRefreshTokenFromCookie(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }
    for (Cookie cookie : request.getCookies()) {
      if (jwtProperties.getRefreshCookieKey().equals(cookie.getName())) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
