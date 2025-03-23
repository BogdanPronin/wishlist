package com.github.bogdanpronin.wishlist.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "DTO для обновления подарка")
data class WishUpdateDto(

    @Schema(description = "Новое название подарка", example = "iPhone 15 Pro Max")
    val title: String? = null,

    @Schema(description = "Новое описание подарка", example = "Цвет: Титановый, Память: 256GB")
    val description: String? = null,

    @Schema(description = "Новый приоритет", example = "2")
    val priority: Int? = null,

    @Schema(description = "Новая дата получения подарка", example = "2025-05-01")
    val dueDate: LocalDate? = null
)
