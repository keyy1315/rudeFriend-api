package com.loltft.rudefriend.jwt_security

import com.fasterxml.jackson.databind.ObjectMapper
import com.loltft.rudefriend.config.JwtProperties
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.fail
import com.loltft.rudefriend.service.AnonymousMemberService
import com.loltft.rudefriend.service.CustomUserDetailService
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.List

@Slf4j
@Component
class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider? = null,
    private val customUserDetailService: CustomUserDetailService? = null,
    private val jwtProperties: JwtProperties? = null,
    private val objectMapper: ObjectMapper? = null,
    private val anonymousMemberService: AnonymousMemberService? = null
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = tokenProvider!!.getAccessTokenFromRequest(request)

            if (StringUtils.hasText(token)
                && tokenProvider.getTokenSubject(token) == jwtProperties!!.accessTokenSubject
            ) {
                if (tokenProvider.validateToken(token)) {
                    val username = tokenProvider.getUsernameFromAccessToken(token)

                    val userDetails = customUserDetailService!!.loadUserByUsername(username)

                    val webDetail = WebAuthenticationDetailsSource().buildDetails(
                        request
                    )
                    log.info("요청 클라이언트 환경 정보 : {}", webDetail)

                    val authenticationToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    )
                    authenticationToken.setDetails(webDetail)

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken)
                }
            } else {
                val anonymousIpAddress = request.remoteAddr
                val grantedAuthorities = listOf<GrantedAuthority?>(
                    SimpleGrantedAuthority(
                        "ROLE_ANONYMOUS"
                    )
                )
                val anonymousAuthentication: Authentication = AnonymousAuthenticationToken(
                    "ANONYMOUS",
                    anonymousIpAddress, grantedAuthorities
                )
                SecurityContextHolder.getContext().setAuthentication(anonymousAuthentication)
                anonymousMemberService!!.saveAnonymousMember(anonymousIpAddress)
            }
        } catch (e: JwtException) {
            log.error("JWT 필터 JwtException : {}", e.message, e)
            handleJwtException(e, response)
            return
        }
        filterChain.doFilter(request, response)
    }

    /**
     * 토큰 검증 오류 메세지를 클라이언트에 직접 반환하기 위한 메소드
     *
     * @param e        발생한 에러
     * @param response 응답
     * @throws IOException 응답 메세지 전달 오류 발생 시
     */
    @Throws(IOException::class)
    private fun handleJwtException(e: JwtException, response: HttpServletResponse) {
        val errorMessage = tokenProvider!!.handleJwtExceptionMessage(e)

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"
        response
            .writer
            .write(objectMapper!!.writeValueAsString(fail<Any?>(errorMessage)))
    }
}
