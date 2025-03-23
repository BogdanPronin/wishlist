package com.github.bogdanpronin.wishlist.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

data class ErrorResponse(
    val message: String,
    val status: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailExists(ex: EmailAlreadyExistsException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = ex.message ?: "Email уже используется",
            status = HttpStatus.CONFLICT.value()
        )
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = ex.message ?: "Некорректный запрос",
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(ex: SecurityException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            message = ex.message ?: "Доступ запрещён",
            status = HttpStatus.UNAUTHORIZED.value()
        )
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

}
