package com.github.bogdanpronin.wishlist.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Запрос на авторизацию")
data class AuthRequest(

    @Schema(description = "Email пользователя", example = "user@example.com")
    val email: String,

    @Schema(description = "Пароль", example = "12345678")
    val password: String
)
