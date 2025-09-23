package com.loltft.rudefriend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class JwtProperties {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.hash-secret}")
  private String hashSecret;

  private final long expiration = 86400000; // 24H
  private final long refreshExpiration = 86400000 * 2; // 48H

  private final String accessTokenSubject = "access_token";
  private final String refreshTokenSubject = "refresh_token";

  private final String claimKey = "memberId";

  private final String accessHeaderName = "AccessToken";
  private final String refreshHeaderName = "RefreshToken";
}
