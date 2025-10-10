package com.loltft.rudefriend.jwt_security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loltft.rudefriend.dto.ApiCommonResponse;
import com.loltft.rudefriend.service.AuthService;
import com.loltft.rudefriend.service.CustomUserDetailService;
import com.loltft.rudefriend.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailService customUserDetailService;
  private final AuthService authService;
  private final TokenHashUtil tokenHashUtil;
  private final MemberService memberService;
  private final ObjectMapper objectMapper;

  /**
   * 해당 accessToken이 만료되었지만 refreshToken이 유효할 경우, accessToken은 없지만 refreshToken이 존재 할 경우
   * <p> 새로운 accessToken 생성하여 클라이언트에 401 status와 함께 전달
   *
   * @param request     클라이언트 요청
   * @param response    클라이언트 응답
   * @param filterChain 필터 체인
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = tokenProvider.getAccessTokenFromRequest(request);
    String refreshToken = tokenProvider.getRefreshTokenFromCookie(request);

    try {
      if (StringUtils.hasText(accessToken)) {
        tokenProvider.validateToken(accessToken);
      } else if (StringUtils.hasText(refreshToken)) {
        handleRefreshToken(response, refreshToken);
        return;
      }
    } catch (ExpiredJwtException e) {
      if (StringUtils.hasText(refreshToken)) {
        handleRefreshToken(response, refreshToken);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  /**
   * 쿠키에 저장 된 refreshToken 검증
   *
   * @param response     응답
   * @param refreshToken 쿠키에 저장 된 refreshToken
   */
  private void handleRefreshToken(HttpServletResponse response,
      String refreshToken) throws IOException {
    try {
      tokenProvider.validateToken(refreshToken);
    } catch (JwtException e) {
      String errorMessage = tokenProvider.handleJwtExceptionMessage(e);

      log.error("유효하지 않은 Refresh Token : {}", e.getMessage());
      handleResponseMessage(response, errorMessage);
    }

    String hashedToken = tokenHashUtil.hashToken(refreshToken);
    var member = memberService.findByRefreshToken(hashedToken);
    if (member == null) {
      logger.error("DB에 존재하지 않는 Refresh Token");

      handleResponseMessage(response, "DB에 존재하지 않는 Refresh Token");
      return;
    }

    authenticateAndIssueNewToken(response, member.getMemberId());
  }

  /**
   * 새로운 accessToken 발급
   *
   * @param response 응답
   * @param memberId refreshToken 으로 조회 한 회원 ID
   */
  private void authenticateAndIssueNewToken(HttpServletResponse response,
      String memberId) throws IOException {
    UserDetails userDetails = customUserDetailService.loadUserByUsername(memberId);
    new AccountStatusUserDetailsChecker().check(userDetails);

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    handleResponseMessage(response, "인증 토큰이 존재하지 않거나 유효하지 않습니다.");
    String newAccessToken = tokenProvider.generateAccessToken(authentication);
    authService.responseTokens(newAccessToken, null, response);
  }

  /**
   * 클라이언트에 전달 할 status, 메세지 핸들링
   *
   * @param response 응답
   * @param message  API 응답 메세지
   */
  private void handleResponseMessage(HttpServletResponse response,
      String message) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    response
        .getWriter()
        .write(objectMapper.writeValueAsString(ApiCommonResponse.fail(message)));
  }
}