package com.loltft.rudefriend.jwt_security;

import com.loltft.rudefriend.service.AuthService;
import com.loltft.rudefriend.service.CustomUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailService customUserDetailService;
  private final AuthService authService;
  private final TokenHashUtil tokenHashUtil;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String accessToken = tokenProvider.getAccessTokenFromRequest(request);

    try {
      if (accessToken != null) {
        tokenProvider.validateToken(accessToken);
      }
    } catch (ExpiredJwtException e) {
      String refreshToken = tokenProvider.getRefreshTokenFromCookie(request);

      if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
        String memberId =
            tokenProvider.getMemberIdFromRefreshToken(tokenHashUtil.hashToken(refreshToken));

        UserDetails userDetails = customUserDetailService.loadUserByUsername(memberId);

        new AccountStatusUserDetailsChecker().check(userDetails);

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        String newAccessToken = tokenProvider.generateAccessToken(authenticationToken);

        authService.responseTokens(newAccessToken, null, response);
      }
    }
    filterChain.doFilter(request, response);
  }
}
