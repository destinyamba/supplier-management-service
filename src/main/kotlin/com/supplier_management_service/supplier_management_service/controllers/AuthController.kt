package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.request.*
import com.supplier_management_service.supplier_management_service.dtos.response.SignupResponse
import com.supplier_management_service.supplier_management_service.exceptions.InvalidCredentialsException
import com.supplier_management_service.supplier_management_service.services.AuthService
import com.supplier_management_service.supplier_management_service.services.AzureBlobStorageService
import jakarta.validation.Valid
import org.apache.coyote.BadRequestException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = ["http://localhost:3000"])
class AuthController(
    private val authService: AuthService
) {

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<SignupResponse> {
        return ResponseEntity.ok(authService.signup(request))
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/signin")
    fun signin(@RequestBody request: SigninRequest): ResponseEntity<Any> {
        try {
            val response = authService.signin(request)
            return ResponseEntity.ok(response)
        } catch (e: InvalidCredentialsException) {
            val errorMessage = mapOf("error" to "Invalid credentials provided")
            return ResponseEntity.status(404).body(errorMessage)
        }
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/password-reset")
    fun handlePasswordReset(@Valid @RequestBody request: PasswordResetRequest): ResponseEntity<MessageResponse> {
        return when {
            request.token == null -> {
                val email = request.email
                if (email != null) {
                    authService.initiatePasswordReset(email)
                }
                ResponseEntity.ok(MessageResponse("Reset instructions sent to $email"))
            }

            else -> {
                val email = request.email
                val token = request.token
                val newPassword = request.newPassword ?: throw BadRequestException("New password is required")
                if (email != null) {
                    authService.completePasswordReset(email, token, newPassword)
                }
                ResponseEntity.ok(MessageResponse("Password reset successfully"))
            }
        }
    }

}
