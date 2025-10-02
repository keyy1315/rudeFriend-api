package com.loltft.rudefriend.service;

import com.loltft.rudefriend.config.JwtProperties;
import com.loltft.rudefriend.dto.member.LoginRequest;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.Member;
import com.loltft.rudefriend.jwt_security.JwtTokenProvider;
import com.loltft.rudefriend.jwt_security.TokenHashUtil;
import com.loltft.rudefriend.repository.member.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final TokenHashUtil tokenHashUtil;
  private final JwtProperties jwtProperties;

  private final MemberRepository memberRepository;

  /**
   * 로그인 인증 이후 발급 된 refreshToken을 DB에 저장
   *
   * <p>생성 된 accessToken은 응답 헤더에 전달, refreshToken은 쿠키에 저장
   *
   * @param loginRequest 로그인 요청 객체
   * @param response     클라이언트 응답
   * @return 로그인 한 회원의 정보 LoginResponse
   */
  @Transactional
  public MemberResponse authenticateMember(
      LoginRequest loginRequest, HttpServletResponse response) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getMemberId(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String accessToken = tokenProvider.generateAccessToken(authentication);
    String refreshToken = tokenProvider.generateRefreshToken();

    responseTokens(accessToken, refreshToken, response);

    String hashedRefreshToken = tokenHashUtil.hashToken(refreshToken);
    Member member = memberRepository
        .findByMemberId(tokenProvider.getUsernameFromAccessToken(accessToken))
        .orElseThrow(() -> new UsernameNotFoundException(loginRequest.getMemberId()));
    member.updateRefreshToken(hashedRefreshToken);

    return MemberResponse.from(member);
  }

  /**
   * 생성 된 토큰들을 클라이언트에 전달
   *
   * @param accessToken  생성된 accessToken
   * @param refreshToken 생성된 refreshToken
   * @param response     클라이언트 응답
   */
  public void responseTokens(
      String accessToken, String refreshToken, HttpServletResponse response) {
    try {
      if (accessToken != null) {
        response.setHeader(jwtProperties.getAccessHeaderName(), accessToken);
      }
      if (refreshToken != null) {
        Cookie cookie = new Cookie(jwtProperties.getRefreshCookieKey(), refreshToken);

        cookie.setHttpOnly(true); // JS 접근 차단
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        cookie.setPath("/"); // 전체 경로에서 사용 가능
        cookie.setMaxAge(Math.toIntExact(jwtProperties.getRefreshExpiration())); // 7일

        response.addCookie(cookie);
      }
    } catch (Exception e) {
      throw new IllegalStateException("토큰 응답 중 오류가 발생하였습니다.");
    }
  }

  @Transactional
  public void logout(String memberId, HttpServletResponse response) {
    if (!StringUtils.hasText(memberId)) {
      throw new AccessDeniedException("로그인 정보가 없습니다.");
    }
    Member member = memberRepository
        .findByMemberId(memberId)
        .orElseThrow(() -> new UsernameNotFoundException(memberId));

    member.updateRefreshToken(null);

    Cookie cookie = new Cookie(jwtProperties.getRefreshCookieKey(), null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }
}
