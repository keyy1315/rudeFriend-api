package com.loltft.rudefriend.jwt_security

import com.fasterxml.jackson.databind.ObjectMapper
import com.loltft.rudefriend.dto.ApiCommonResponse.Companion.fail
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json;charset=UTF-8"
        val body = fail<Any?>("접근 권한이 없습니다.")
        response.writer.write(ObjectMapper().writeValueAsString(body))
    }
}
