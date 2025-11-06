package com.loltft.rudefriend.jwt_security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.loltft.rudefriend.config.JwtProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenHashUtil {

  private final JwtProperties jwtProperties;

  private static final String HASH_ALGORITHM = "HmacSHA256";

  public String hashToken(String token) {
    if (!StringUtils.hasText(token)) {
      throw new IllegalStateException("암호화 하기 위한 토큰이 비어 있습니다.");
    }
    try {
      Mac mac = Mac.getInstance(HASH_ALGORITHM);
      byte[] key = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
      SecretKeySpec secret = new SecretKeySpec(key, HASH_ALGORITHM);

      mac.init(secret);
      byte[] hash = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (Exception e) {
      throw new IllegalStateException("Refresh 토큰 해싱 실패");
    }
  }
}
