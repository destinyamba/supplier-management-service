package com.supplier_management_service.supplier_management_service.config

import com.supplier_management_service.supplier_management_service.models.ErrorMessage
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalErrorHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        NoHandlerFoundException::class
    )
    fun handleNotFound(request: HttpServletRequest?, error: Exception?): ErrorMessage {
        return ErrorMessage.from("Not Found")
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(
        AccessDeniedException::class
    )
    fun handleAccessDenied(request: HttpServletRequest?, error: Exception?): ErrorMessage {
        return ErrorMessage.from("Permission denied")
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(
        Throwable::class
    )
    fun handleInternalError(request: HttpServletRequest?, error: Exception): ErrorMessage {
        return ErrorMessage.from(error.message)
    }
}