package com.loltft.rudefriend.jwt_security

import com.fasterxml.jackson.databind.ObjectMapper
import com.loltft.rudefriend.config.JwtProperties
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.fail
import com.loltft.rudefriend.service.CustomUserDetailService
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider,
    private val customUserDetailService: CustomUserDetailService,
    private val jwtProperties: JwtProperties,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = tokenProvider.getAccessTokenFromRequest(request)

            if (StringUtils.hasText(token)
                && tokenProvider.getTokenSubject(token) == jwtProperties.accessTokenSubject
            ) {
                if (tokenProvider.validateToken(token)) {
                    val username = tokenProvider.getUsernameFromAccessToken(token)

                    val userDetails = customUserDetailService.loadUserByUsername(username)

                    val webDetail = WebAuthenticationDetailsSource().buildDetails(
                        request
                    )
                    log.info("요청 클라이언트 환경 정보 : {}", webDetail)

                    val authenticationToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authenticationToken.details = webDetail

                    SecurityContextHolder.getContext().authentication = authenticationToken
                }
            } else {
                val anonymousIpAddress = getClientIpAddress(request)
                val anonymousUserDetails =
                    customUserDetailService.loadUserByIpAddress(anonymousIpAddress)

                val authenticationToken = UsernamePasswordAuthenticationToken(
                    anonymousUserDetails,
                    null,
                    anonymousUserDetails.authorities
                )
                authenticationToken.details =
                    WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
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
        val errorMessage = tokenProvider.handleJwtExceptionMessage(e)

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"
        response
            .writer
            .write(objectMapper.writeValueAsString(fail<Any?>(errorMessage)))
    }


    private fun getClientIpAddress(request: HttpServletRequest): String {
        // 1. X-Forwarded-For 헤더 확인 (가장 일반적)
        var ipAddress = request.getHeader("X-Forwarded-For")

        // 2. X-Forwarded-For가 없으면 다른 헤더들 확인
        if (ipAddress.isNullOrEmpty() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("Proxy-Client-IP")
        }
        if (ipAddress.isNullOrEmpty() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ipAddress.isNullOrEmpty() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ipAddress.isNullOrEmpty() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR")
        }

        // 3. 위의 모든 헤더가 없으면 remoteAddr 사용
        if (ipAddress.isNullOrEmpty() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.remoteAddr
        }

        // 4. X-Forwarded-For에 여러 IP가 있는 경우 (쉼표로 구분)
        // 형식: client, proxy1, proxy2
        // 첫 번째가 실제 클라이언트 IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim()
        }

        return ipAddress ?: "unknown"
    }
}
