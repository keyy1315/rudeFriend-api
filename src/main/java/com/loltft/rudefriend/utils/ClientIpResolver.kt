package com.loltft.rudefriend.utils

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class ClientIpResolver {
    fun resolve(request: HttpServletRequest): String {
        val candidates = listOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        )

        var ipAddress = candidates
            .asSequence()
            .mapNotNull { header -> request.getHeader(header)?.takeUnless { it.equals("unknown", true) } }
            .firstOrNull()

        if (ipAddress.isNullOrEmpty() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.remoteAddr
        }

        if (!ipAddress.isNullOrEmpty() && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim()
        }

        return ipAddress ?: "unknown"
    }
}
