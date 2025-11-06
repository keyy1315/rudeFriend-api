package com.loltft.rudefriend.jwt_security

import com.loltft.rudefriend.config.JwtProperties
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Slf4j
@Component
@RequiredArgsConstructor
class JwtTokenProvider(private val jwtProperties: JwtProperties? = null) {

    private val signingKey: SecretKey
        get() {
            val keyBytes =
                jwtProperties!!.secret.toByteArray(StandardCharsets.UTF_8)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    /**
     * 토큰 검증 Exception 의 에러 메세지 설정을 위한 메소드
     *
     * @param e Exception
     * @return errorMessage
     */
    fun handleJwtExceptionMessage(e: Exception): String? {
        val errorMessage: String?
        when (e) {
            is ExpiredJwtException -> errorMessage = "토큰이 만료되었습니다."
            is MalformedJwtException -> errorMessage = "토큰이 잘못되었거나 변조되었습니다."
            is SignatureException -> errorMessage = "서명이 맞지 않습니다."
            is UnsupportedJwtException -> errorMessage = "해당 토큰의 포맷은 지원하지 않습니다."
            is IllegalArgumentException -> errorMessage = "토큰의 클레임을 읽을 수 없습니다."
            else -> errorMessage = e.message
        }
        return errorMessage
    }


    /**
     * AccessToken 생성
     *
     * @param authentication 인증 객체
     * @return 생성 된 AccessToken
     */
    fun generateAccessToken(authentication: Authentication): String? {
        val userDetails = authentication.principal as UserDetails

        val now = Date()
        val expireDate = Date(now.time + jwtProperties!!.expiration)

        return Jwts.builder()
            .claim(jwtProperties.claimKey, userDetails.username)
            .subject(jwtProperties.accessTokenSubject)
            .issuedAt(now)
            .expiration(expireDate)
            .signWith(this.signingKey)
            .compact()
    }

    /**
     * accessToken 재발급을 위한 refreshToken 생성
     *
     * @return 생성 된 refreshToken
     */
    fun generateRefreshToken(): String? {
        val now = Date()
        val expireDate = Date(now.time + jwtProperties!!.refreshExpiration)

        return Jwts.builder()
            .subject(jwtProperties.refreshTokenSubject)
            .issuedAt(now)
            .expiration(expireDate)
            .signWith(this.signingKey)
            .compact()
    }

    /**
     * @param token 토큰
     * @return 토큰에서 추출한 관리자 ID
     * @throws AuthenticationCredentialsNotFoundException args에 토큰이 빈 값일 때
     */
    fun getUsernameFromAccessToken(token: String?): String? {
        if (!StringUtils.hasText(token)) {
            throw AuthenticationCredentialsNotFoundException("토큰 정보가 없습니다.")
        }
        try {
            val claims = Jwts.parser().verifyWith(this.signingKey).build().parseSignedClaims(token)
                .getPayload()
            return claims.get(jwtProperties!!.claimKey).toString()
        } catch (e: Exception) {
            throw JwtException(handleJwtExceptionMessage(e))
        }
    }

    /**
     * @param token 토큰
     * @return 토큰의 subject - access / refresh
     * @throws AuthenticationCredentialsNotFoundException args에 토큰이 빈 값일 때
     */
    fun getTokenSubject(token: String?): String? {
        if (!StringUtils.hasText(token)) {
            throw AuthenticationCredentialsNotFoundException("토큰 정보가 없습니다.")
        }
        try {
            val claims = Jwts.parser().verifyWith(this.signingKey).build().parseSignedClaims(token)
                .getPayload()
            return claims.getSubject()
        } catch (e: Exception) {
            throw JwtException(handleJwtExceptionMessage(e))
        }
    }

    /**
     * @param request HttpServletRequest
     * @return token
     */
    fun getAccessTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION)
        if (StringUtils.hasText(bearerToken) && bearerToken!!.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length)
        }
        return null
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token 검증 할 토큰
     * @return 토큰 유효성 (true, false)
     */
    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parser().verifyWith(this.signingKey).build().parseSignedClaims(token)
            return true
        } catch (e: JwtException) {
            throw e
        } catch (_: Exception) {
            throw IllegalStateException("JWT 파싱 오류")
        }
    }

    /**
     * 요청 쿠키에서 refreshToken 값을 추출
     *
     * @param request 요청
     * @return refreshToken
     */
    fun getRefreshTokenFromCookie(request: HttpServletRequest): String? {
        if (request.cookies == null) {
            return null
        }
        for (cookie in request.cookies) {
            if (jwtProperties!!.refreshCookieKey == cookie.name) {
                return cookie.value
            }
        }
        return null
    }

    companion object {
        private const val BEARER = "Bearer "
        private const val AUTHORIZATION = "Authorization"
    }
}
