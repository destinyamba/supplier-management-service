package com.supplier_management_service.supplier_management_service.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.supplier_management_service.supplier_management_service.models.ErrorMessage
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler

@Component
class AuthenticationErrorHandler(private val mapper: ObjectMapper) : AuthenticationEntryPoint {

    @ExceptionHandler
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val errorMessage: ErrorMessage = ErrorMessage.from("Requires authentication")
        val json = mapper.writeValueAsString(errorMessage)

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(json)
        response.flushBuffer()
    }
}
