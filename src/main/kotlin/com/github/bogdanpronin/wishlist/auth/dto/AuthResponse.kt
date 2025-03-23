package com.github.bogdanpronin.wishlist.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с JWT токеном")
data class AuthResponse(

    @Schema(description = "JWT access токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val accessToken: String,

    @Schema(description = "JWT refresh токен", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val refreshToken: String
)
