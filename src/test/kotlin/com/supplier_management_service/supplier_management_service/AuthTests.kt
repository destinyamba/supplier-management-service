package com.supplier_management_service.supplier_management_service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.supplier_management_service.supplier_management_service.controllers.AuthController
import com.supplier_management_service.supplier_management_service.dtos.request.PasswordResetRequest
import com.supplier_management_service.supplier_management_service.dtos.request.SigninRequest
import com.supplier_management_service.supplier_management_service.dtos.request.SignupRequest
import com.supplier_management_service.supplier_management_service.dtos.response.SignupResponse
import com.supplier_management_service.supplier_management_service.exceptions.InvalidCredentialsException
import com.supplier_management_service.supplier_management_service.models.BusinessType
import com.supplier_management_service.supplier_management_service.models.Role
import com.supplier_management_service.supplier_management_service.services.AuthService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class AuthControllerTests {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var authService: AuthService

    @InjectMocks
    private lateinit var authController: AuthController

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .build()
    }
    
    @Test
    fun `signup returns 400 with invalid email`() {
        val invalidRequest = SignupRequestBuilder()
            .withEmail("invalid-email")
            .build()

        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }


    @Test
    fun `sign in returns 404 with invalid credentials`() {
        val request = SigninRequestBuilder().validRequest().build()

        whenever(authService.signin(any()))
            .thenThrow(InvalidCredentialsException("Invalid credentials"))

        mockMvc.perform(
            post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Invalid credentials provided"))
    }

    @Test
    fun `password reset completion returns 200`() {
        val request = PasswordResetRequest(
            email = "user@example.com",
            token = "reset-token",
            newPassword = "newPassword123!"
        )

        mockMvc.perform(
            post("/api/v1/auth/password-reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("Password reset successfully"))

        verify(authService).completePasswordReset("user@example.com", "reset-token", "newPassword123!")
    }


    @Test
    fun `all endpoints include CORS headers`() {
        val endpoints = listOf(
            post("/api/v1/auth/signup"),
            post("/api/v1/auth/signin"),
            post("/api/v1/auth/password-reset")
        )

        endpoints.forEach { endpoint ->
            mockMvc.perform(
                endpoint
                    .header("Origin", "http://localhost:3000")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(header().exists("Access-Control-Allow-Origin"))
        }
    }
}

class SignupRequestBuilder {
    private var email = "user@example.com"
    private var password = "ValidPass123!"
    private var organization = "Test Org"

    fun validRequest() = this
    fun withEmail(email: String) = apply { this.email = email }
    fun build() = SignupRequest(
        email, password, organization,
        role = Role.ADMIN,
        businessType = BusinessType.CLIENT
    )
}

class SigninRequestBuilder {
    private var email = "user@example.com"
    private var password = "ValidPass123!"

    fun validRequest() = this
    fun build() = SigninRequest(email, password)
}

class SignupResponseBuilder {
    fun build() = SignupResponse(
        id = UUID.randomUUID().toString(),
        email = "email",
        name = "name",
        role = Role.ADMIN,
        businessType = BusinessType.CLIENT,
        createdAt = LocalDateTime.now(),
        lastSignIn = LocalDateTime.now(),
        token = "token"
    )
}