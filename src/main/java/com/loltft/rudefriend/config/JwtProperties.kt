package com.loltft.rudefriend.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val hashSecret: String,
    val expiration: Long = 86_400_000, // 24H
    val refreshExpiration: Long = 604_800_000, // 7Day
    val accessTokenSubject: String = "access_token",
    val refreshTokenSubject: String = "refresh_token",
    val claimKey: String = "memberId",
    val accessHeaderName: String = "AccessToken",
    val refreshCookieKey: String = "RefreshToken"
)