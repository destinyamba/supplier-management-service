package com.supplier_management_service.supplier_management_service.config.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${spring.auth.jwt.secret}")
    private lateinit var secret: String

    private var expiration: Long = 864000000 // 10 days (in milliseconds)

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = mapOf(
            "roles" to userDetails.authorities.map { it.authority }
        )

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun extractUsername(token: String): String? {
        return extractClaim(token) { it.body.subject }
    }

    fun extractExpiration(token: String): Date? {
        return extractClaim(token) { it.body.expiration }
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }


    fun getRolesFromToken(token: String): List<String> {
        return extractClaim(token) {
            it.body["roles"] as List<String>
        } ?: emptyList()
    }

    private fun <T> extractClaim(token: String, claimsResolver: (Jws<Claims>) -> T): T? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            claimsResolver(claims)
        } catch (e: Exception) {
            null
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token)?.before(Date()) ?: true
    }
}