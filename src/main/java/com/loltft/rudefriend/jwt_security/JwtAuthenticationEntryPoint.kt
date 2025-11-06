package com.loltft.rudefriend.jwt_security

import com.fasterxml.jackson.databind.ObjectMapper
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.fail
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
@RequiredArgsConstructor
@Slf4j
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper? = null
) : AuthenticationEntryPoint {

    private val log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint::class.java)

    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest?, response: HttpServletResponse,
        e: AuthenticationException
    ) {
        log.info(
            "message: {}, className: {}",
            e.message,
            e.javaClass.getName()
        )

        val errorMessage = when (e) {
            is InsufficientAuthenticationException -> "인증 토큰이 존재하지 않거나 유효하지 않습니다."
            is DisabledException -> "계정이 비활성화되어 있습니다."
            is AuthenticationCredentialsNotFoundException -> "인증 토큰이 없습니다."
            else -> e.message ?: "인증 오류가 발생했습니다."
        }

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"
        response
            .writer
            .write(objectMapper!!.writeValueAsString(fail<Any?>(errorMessage)))
    }
}
