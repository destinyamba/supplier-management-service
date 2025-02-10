package com.supplier_management_service.supplier_management_service.dtos

import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailExists(ex: EmailAlreadyExistsException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(mapOf("error" to ex.message!!))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidationExceptions(ex: ConstraintViolationException): ResponseEntity<Map<String, String>> {
        val errors = mutableMapOf<String, String>()
        ex.constraintViolations.forEach {
            errors[it.propertyPath.last().name] = it.message
        }
        return ResponseEntity.badRequest().body(errors)
    }
}