package com.github.bogdanpronin.wishlist.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "Пользователь системы")
data class UserDto(

    @Schema(description = "ID пользователя", example = "1a3c776e-f59e-4120-80e3-fb0e099ec8cc")
    val id: UUID,

    @Schema(description = "Email пользователя", example = "user@example.com")
    val email: String,

    @Schema(description = "Полное имя", example = "Иван Иванов")
    val fullName: String?
)
