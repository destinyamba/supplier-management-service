package com.supplier_management_service.supplier_management_service.dtos

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MultipartException

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

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        val body = mapOf(
            "message" to "Processing failed: ${ex.message ?: "Unknown error"}"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }

    @ExceptionHandler(MultipartException::class)
    fun handleFileUploadException(ex: MultipartException): ResponseEntity<Map<String, String>> {
        val body = mapOf(
            "message" to "File upload failed: ${ex.message ?: "Check file size and format"}"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }
}