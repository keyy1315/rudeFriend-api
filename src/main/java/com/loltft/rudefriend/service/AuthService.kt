package com.loltft.rudefriend.service

import com.loltft.rudefriend.config.JwtProperties
import com.loltft.rudefriend.dto.member.LoginRequest
import com.loltft.rudefriend.dto.member.MemberResponse
import com.loltft.rudefriend.dto.member.MemberResponse.Companion.from
import com.loltft.rudefriend.jwt_security.JwtTokenProvider
import com.loltft.rudefriend.jwt_security.TokenHashUtil
import com.loltft.rudefriend.repository.member.MemberRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import java.util.function.Supplier

@Service
class AuthService {
    private val authenticationManager: AuthenticationManager? = null
    private val tokenProvider: JwtTokenProvider? = null
    private val tokenHashUtil: TokenHashUtil? = null
    private val jwtProperties: JwtProperties? = null

    private val memberRepository: MemberRepository? = null

    /**
     * 로그인 인증 이후 발급 된 refreshToken을 DB에 저장
     *
     *
     * 생성 된 accessToken은 응답 헤더에 전달, refreshToken은 쿠키에 저장
     *
     * @param loginRequest 로그인 요청 객체
     * @param response     클라이언트 응답
     * @return 로그인 한 회원의 정보 LoginResponse
     */
    @Transactional
    fun authenticateMember(
        loginRequest: LoginRequest, response: HttpServletResponse
    ): MemberResponse? {
        val authentication = authenticationManager!!.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.memberId, loginRequest.password
            )
        )

        SecurityContextHolder.getContext().setAuthentication(authentication)

        val accessToken = tokenProvider!!.generateAccessToken(authentication)
        val refreshToken = tokenProvider.generateRefreshToken()

        responseTokens(accessToken, refreshToken, response)

        val hashedRefreshToken = tokenHashUtil!!.hashToken(refreshToken)
        val member = memberRepository!!
            .findByMemberId(tokenProvider.getUsernameFromAccessToken(accessToken))
            ?.orElseThrow<UsernameNotFoundException?>(Supplier {
                UsernameNotFoundException(
                    loginRequest.memberId
                )
            })
        member?.updateRefreshToken(hashedRefreshToken)

        return member?.let { from(it) }
    }

    /**
     * 생성 된 토큰들을 클라이언트에 전달
     *
     * @param accessToken  생성된 accessToken
     * @param refreshToken 생성된 refreshToken
     * @param response     클라이언트 응답
     */
    fun responseTokens(
        accessToken: String?, refreshToken: String?, response: HttpServletResponse
    ) {
        try {
            if (accessToken != null) {
                response.setHeader(jwtProperties!!.accessHeaderName, accessToken)
            }
            if (refreshToken != null) {
                val cookie = Cookie(jwtProperties!!.refreshCookieKey, refreshToken)

                cookie.isHttpOnly = true // JS 접근 차단
                cookie.secure = true // HTTPS 환경에서만 전송
                cookie.path = "/" // 전체 경로에서 사용 가능
                cookie.maxAge = Math.toIntExact(jwtProperties.refreshExpiration) // 7일

                response.addCookie(cookie)
            }
        } catch (e: Exception) {
            throw IllegalStateException("토큰 응답 중 오류가 발생하였습니다.")
        }
    }

    @Transactional
    fun logout(memberId: String?, response: HttpServletResponse) {
        if (!StringUtils.hasText(memberId)) {
            throw AccessDeniedException("로그인 정보가 없습니다.")
        }
        val member = memberRepository!!
            .findByMemberId(memberId)
            ?.orElseThrow(Supplier { UsernameNotFoundException(memberId) })

        member?.updateRefreshToken(null)

        val cookie = Cookie(jwtProperties!!.refreshCookieKey, null)
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.maxAge = 0
        response.addCookie(cookie)
    }
}
