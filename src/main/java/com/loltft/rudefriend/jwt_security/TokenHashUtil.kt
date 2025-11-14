package com.loltft.rudefriend.jwt_security

import com.loltft.rudefriend.config.JwtProperties
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class TokenHashUtil(private val jwtProperties: JwtProperties? = null) {

    fun hashToken(token: String?): String? {
        check(StringUtils.hasText(token)) { "암호화 하기 위한 토큰이 비어 있습니다." }
        try {
            val mac = Mac.getInstance(HASH_ALGORITHM)
            val key = jwtProperties!!.secret.toByteArray(StandardCharsets.UTF_8)
            val secret = SecretKeySpec(key, HASH_ALGORITHM)

            mac.init(secret)
            val hash = mac.doFinal(token!!.toByteArray(StandardCharsets.UTF_8))
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
        } catch (_: Exception) {
            throw IllegalStateException("Refresh 토큰 해싱 실패")
        }
    }

    companion object {
        private const val HASH_ALGORITHM = "HmacSHA256"
    }
}
