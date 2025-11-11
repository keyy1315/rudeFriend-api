package com.loltft.rudefriend.exception

import com.loltft.rudefriend.dto.ApiCommonResponse
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.fail
import io.jsonwebtoken.JwtException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiCommonResponse<String?>?> {
        log.error("Exception 오류 status - 500 : {}", e.message, e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body<ApiCommonResponse<String?>?>(fail<String?>("서버 내부 오류가 발생했습니다."))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(
        e: IllegalStateException
    ): ResponseEntity<ApiCommonResponse<String?>?> {
        log.error("IllegalStateException 오류 status - 500 : {}", e.message, e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body<ApiCommonResponse<String?>?>(fail<String?>(e.message))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        e: NoSuchElementException
    ): ResponseEntity<ApiCommonResponse<String?>?> {
        log.error(
            "NoSuchElementException 오류 status - 500 : {}",
            e.message,
            e
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body<ApiCommonResponse<String?>?>(fail<String?>(e.message))
    }

    /** ============================ 인증 인가 에러 ============================ */
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(
        e: UsernameNotFoundException
    ): ResponseEntity<ApiCommonResponse<String?>?> {
        log.error(
            "UsernameNotFoundException 오류 status - 404 ID : {}",
            e.message,
            e
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body<ApiCommonResponse<String?>?>(fail<String?>("회원 정보를 찾을 수 없습니다 ID : " + e.message))
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(e: JwtException): ResponseEntity<ApiCommonResponse<String?>?> {
        log.error("JwtException 오류 status - 401 : {}", e.message, e)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body<ApiCommonResponse<String?>?>(fail<String?>(e.message))
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        e: AuthenticationException
    ): ResponseEntity<ApiCommonResponse<Any>?> {

        log.error("AuthenticationException 발생 - 401 : {}", e.message, e)

        val errorMessage = when (e) {
            is BadCredentialsException -> "아이디 또는 비밀번호가 틀렸습니다."
            is DisabledException -> "계정이 비활성화되어 있습니다."
            else -> e.message ?: "인증 오류가 발생했습니다."
        }

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(fail<Any>(errorMessage))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        e: AccessDeniedException
    ): ResponseEntity<ApiCommonResponse<Any?>?> {
        log.error("AccessDeniedException 오류 status - 401 : {}", e.message, e)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body<ApiCommonResponse<Any?>?>(fail<Any?>(e.message))
    }
}
