package com.github.bogdanpronin.wishlist.auth.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.refreshSecret}") private val refreshSecret: String
) {
    private val accessExpirationMs = 15 * 60 * 1000 // 15 минут
    private val refreshExpirationMs = 7 * 24 * 60 * 60 * 1000 // 7 дней

    fun generateAccessToken(userId: UUID): String =
        generateToken(userId, accessExpirationMs, secret)

    fun generateRefreshToken(userId: UUID): String =
        generateToken(userId, refreshExpirationMs, refreshSecret)

    private fun generateToken(userId: UUID, expiration: Int, key: String): String {
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(Keys.hmacShaKeyFor(key.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUserIdFromAccess(token: String): UUID =
        extractUserId(token, secret)

    fun extractUserIdFromRefresh(token: String): UUID =
        extractUserId(token, refreshSecret)

    fun extractUserId(token: String, key: String): UUID =
        UUID.fromString(
            Jwts.parserBuilder()
                .setSigningKey(key.toByteArray())
                .build()
                .parseClaimsJws(token)
                .body.subject
        )
}

