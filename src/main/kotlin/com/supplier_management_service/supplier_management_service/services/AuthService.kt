package com.supplier_management_service.supplier_management_service.services

import com.supplier_management_service.supplier_management_service.config.security.JwtUtil
import com.supplier_management_service.supplier_management_service.dtos.request.PasswordResetToken
import com.supplier_management_service.supplier_management_service.dtos.request.SigninRequest
import com.supplier_management_service.supplier_management_service.exceptions.EmailAlreadyExistsException
import com.supplier_management_service.supplier_management_service.dtos.request.SignupRequest
import com.supplier_management_service.supplier_management_service.dtos.response.SigninResponse
import com.supplier_management_service.supplier_management_service.dtos.response.SignupResponse
import com.supplier_management_service.supplier_management_service.exceptions.InvalidCredentialsException
import com.supplier_management_service.supplier_management_service.models.Role
import com.supplier_management_service.supplier_management_service.models.User
import com.supplier_management_service.supplier_management_service.repositories.PasswordResetTokenRepository
import com.supplier_management_service.supplier_management_service.repositories.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID

@Service
class AuthService(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    @Value("\${spring.auth.jwt.secret}") private val jwtSecret: String,
    private val passwordResetTokenRepository: PasswordResetTokenRepository
) {

    private val jwtExpirationMs = 86400000 // 1 day
    private val jwtSecretKey: Key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    private val logger: Logger = LoggerFactory.getLogger(AuthService::class.java)

    fun signup(request: SignupRequest): SignupResponse {
        logger.info("Signup request received: $request")
        validateSignupRequest(request)

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            role = Role.ADMIN,
            businessType = request.businessType
        )

        val savedUser = userRepository.save(user)

        return SignupResponse(
            id = savedUser.id!!,
            email = savedUser.email,
            name = savedUser.name,
            role = savedUser.role,
            businessType = savedUser.businessType,
            createdAt = savedUser.createdAt,
            lastSignIn = savedUser.lastSignIn
        )
    }

    fun signin(request: SigninRequest): SigninResponse {
        logger.info("Signin request received: ${request.email}")
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidCredentialsException("Invalid email or password")
        }

        // Generate JWT token (implementation depends on your JWT setup)
        val token = generateToken(user)

        return SigninResponse(
            id = user.id!!,
            email = user.email,
            name = user.name,
            role = user.role,
            businessType = user.businessType,
            token = token
        )
    }

    fun initiatePasswordReset(email: String) {
        val user = userRepository.findByEmail(email)
            ?: throw BadRequestException("User with email $email not found")

        val token = UUID.randomUUID().toString()
        logger.info("Generated token: $token")
        user.id?.let {
            PasswordResetToken(
                userId = it,
                token = token,
                expiryDate = Instant.now().plus(1, ChronoUnit.HOURS)
            )
        }?.let {
            passwordResetTokenRepository.save(
                it
            )
        }

        emailService.sendResetToken(email, token)
    }

    fun completePasswordReset(email: String, token: String, newPassword: String) {
        val user = userRepository.findByEmail(email)
            ?: throw BadRequestException("User not found")

        val resetToken = user.id?.let { passwordResetTokenRepository.findByUserIdAndToken(it, token) }
            ?: throw BadRequestException("Invalid reset token")

        if (resetToken.expiryDate.isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken)
            throw BadRequestException("Token has expired")
        }

        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        passwordResetTokenRepository.delete(resetToken)
    }

    fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            user.getAuthorities()
        )
    }

    private fun User.getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"))  // Using ROLE_ prefix
    }

    private fun validateSignupRequest(request: SignupRequest) {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException("Email ${request.email} is already in use")
        }
    }

//    private fun generateToken(user: User): String {
//        return Jwts.builder()
//            .setSubject(user.email)
//            .claim("role", user.role.toString())
//            .setIssuedAt(Date())
//            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationMs))
//            .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
//            .compact()
//    }

    fun generateToken(user: User): String {
        return jwtUtil.generateToken(
            org.springframework.security.core.userdetails.User(
                user.email,
                user.password,
                listOf(SimpleGrantedAuthority("ROLE_${user.role}"))
            )
        )
    }
}