package com.loltft.rudefriend.jwt_security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loltft.rudefriend.config.JwtProperties;
import com.loltft.rudefriend.dto.ApiCommonResponse;
import com.loltft.rudefriend.service.CustomUserDetailService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailService customUserDetailService;
  private final JwtProperties jwtProperties;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = tokenProvider.getAccessTokenFromRequest(request);

      if (StringUtils.hasText(token)
          && tokenProvider.getTokenSubject(token).equals(jwtProperties.getAccessTokenSubject())) {
        if (tokenProvider.validateToken(token)) {
          String username = tokenProvider.getUsernameFromAccessToken(token);

          UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

          WebAuthenticationDetails webDetail =
              new WebAuthenticationDetailsSource().buildDetails(request);
          log.info("요청 클라이언트 환경 정보 : {}", webDetail);

          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authenticationToken.setDetails(webDetail);

          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      } else {
        List<GrantedAuthority> grantedAuthorities =
            List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        Authentication anonymousAuthentication =
            new AnonymousAuthenticationToken("ANONYMOUS", "anonymous", grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthentication);
      }
    } catch (JwtException e) {
      log.error("JWT 필터 JwtException : {}", e.getMessage(), e);
      handleJwtException(e, response);
      return;
    }
    filterChain.doFilter(request, response);
  }

  /**
   * 토큰 검증 오류 메세지를 클라이언트에 직접 반환하기 위한 메소드
   *
   * @param e 발생한 에러
   * @param response 응답
   * @throws IOException 응답 메세지 전달 오류 발생 시
   */
  private void handleJwtException(JwtException e, HttpServletResponse response) throws IOException {
    String errorMessage = tokenProvider.handleJwtExceptionMessage(e);

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    response
        .getWriter()
        .write(objectMapper.writeValueAsString(ApiCommonResponse.fail(errorMessage)));
  }
}
