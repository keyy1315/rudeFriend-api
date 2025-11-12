package com.loltft.rudefriend.jwt_security

import com.fasterxml.jackson.databind.ObjectMapper
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.fail
import com.loltft.rudefriend.service.AuthService
import com.loltft.rudefriend.service.CustomUserDetailService
import com.loltft.rudefriend.service.MemberService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AccountStatusUserDetailsChecker
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class RefreshTokenFilter(
    private val tokenProvider: JwtTokenProvider? = null,
    private val customUserDetailService: CustomUserDetailService? = null,
    private val authService: AuthService? = null,
    private val tokenHashUtil: TokenHashUtil? = null,
    private val memberService: MemberService? = null,
    private val objectMapper: ObjectMapper? = null
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RefreshTokenFilter::class.java)

    /**
     * 해당 accessToken이 만료되었지만 refreshToken이 유효할 경우, accessToken은 없지만 refreshToken이 존재 할 경우
     *
     *  새로운 accessToken 생성하여 클라이언트에 401 status와 함께 전달
     *
     * @param request     클라이언트 요청
     * @param response    클라이언트 응답
     * @param filterChain 필터 체인
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = tokenProvider!!.getAccessTokenFromRequest(request)
        val refreshToken = tokenProvider.getRefreshTokenFromCookie(request)

        try {
            if (StringUtils.hasText(accessToken)) {
                tokenProvider.validateToken(accessToken)
            } else if (StringUtils.hasText(refreshToken)) {
                handleRefreshToken(response, refreshToken)
                return
            }
        } catch (_: ExpiredJwtException) {
            if (StringUtils.hasText(refreshToken)) {
                handleRefreshToken(response, refreshToken)
                return
            }
        }
        filterChain.doFilter(request, response)
    }

    /**
     * 쿠키에 저장 된 refreshToken 검증
     *
     * @param response     응답
     * @param refreshToken 쿠키에 저장 된 refreshToken
     */
    @Throws(IOException::class)
    private fun handleRefreshToken(
        response: HttpServletResponse,
        refreshToken: String?
    ) {
        try {
            tokenProvider!!.validateToken(refreshToken)
        } catch (e: JwtException) {
            val errorMessage = tokenProvider!!.handleJwtExceptionMessage(e)

            log.error("유효하지 않은 Refresh Token : {}", e.message)
            handleResponseMessage(response, errorMessage)
            return
        }

        val hashedToken = tokenHashUtil!!.hashToken(refreshToken)
        val member = memberService!!.findByRefreshToken(hashedToken)
        if (member == null) {
            logger.error("DB에 존재하지 않는 Refresh Token")

            handleResponseMessage(response, "DB에 존재하지 않는 Refresh Token")
            return
        }

        authenticateAndIssueNewToken(response, member.memberId)
    }

    /**
     * 새로운 accessToken 발급
     *
     * @param response 응답
     * @param memberId refreshToken 으로 조회 한 회원 ID
     */
    @Throws(IOException::class)
    private fun authenticateAndIssueNewToken(
        response: HttpServletResponse,
        memberId: String?
    ) {
        val userDetails = customUserDetailService!!.loadUserByUsername(memberId)
        AccountStatusUserDetailsChecker().check(userDetails)

        val authentication = UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        )
        SecurityContextHolder.getContext().setAuthentication(authentication)

        handleResponseMessage(response, "인증 토큰이 존재하지 않거나 유효하지 않습니다.")
        val newAccessToken = tokenProvider!!.generateAccessToken(authentication)
        authService!!.responseTokens(newAccessToken, null, response)
    }

    /**
     * 클라이언트에 전달 할 status, 메세지 핸들링
     *
     * @param response 응답
     * @param message  API 응답 메세지
     */
    @Throws(IOException::class)
    private fun handleResponseMessage(
        response: HttpServletResponse,
        message: String?
    ) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        response.setContentType("application/json;charset=UTF-8")
        response
            .getWriter()
            .write(objectMapper!!.writeValueAsString(fail<Any?>(message)))
    }
}
