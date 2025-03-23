package com.github.bogdanpronin.wishlist.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.*

@Schema(description = "DTO для создания подарка")
data class WishCreateDto(

    @Schema(description = "Название подарка", example = "iPhone 15 Pro Max")
    val title: String,

    @Schema(description = "Описание подарка", example = "Цвет: Титановый, Память: 256GB")
    val description: String? = null,

    @Schema(description = "Приоритет подарка (чем выше, тем важнее)", example = "1")
    val priority: Int? = null,

    @Schema(description = "Желаемая дата получения подарка", example = "2025-05-01")
    val dueDate: LocalDate? = null,

    @Schema(description = "ID списка желаний, в который добавляется подарок", example = "13ec6f35-91ce-4a9d-b3a7-bc497c6a301d")
    val wishListId: UUID
)
