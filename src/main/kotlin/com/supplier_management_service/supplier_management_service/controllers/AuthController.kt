package com.supplier_management_service.supplier_management_service.controllers

import com.supplier_management_service.supplier_management_service.dtos.request.*
import com.supplier_management_service.supplier_management_service.dtos.response.SignupResponse
import com.supplier_management_service.supplier_management_service.exceptions.InvalidCredentialsException
import com.supplier_management_service.supplier_management_service.services.AuthService
import jakarta.validation.Valid
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
    fun handlePasswordReset(@RequestBody request: PasswordResetRequest): ResponseEntity<MessageResponse> {
        return when {
            request.token == null -> {
                authService.initiatePasswordReset(request.email)
                ResponseEntity.ok(MessageResponse("Reset instructions sent to ${request.email}"))
            }

            else -> {
                authService.completePasswordReset(request.email, request.token, request.newPassword!!)
                ResponseEntity.ok(MessageResponse("Password reset successfully"))
            }
        }
    }

}
