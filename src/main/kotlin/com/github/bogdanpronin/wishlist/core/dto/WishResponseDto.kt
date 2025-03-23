package com.github.bogdanpronin.wishlist.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Подарок в списке желаний")
data class WishResponseDto(

    @Schema(description = "ID подарка", example = "8fa7b5ee-6f64-4ac9-9b3f-6b53ef0a515d")
    val id: UUID,

    @Schema(description = "Название подарка", example = "Наушники Sony")
    val title: String,

    @Schema(description = "Описание подарка", example = "Модель WH-1000XM5, чёрные")
    val description: String? = null,

    @Schema(description = "Приоритет от 1 до 5", example = "3")
    val priority: Int? = null,

    @Schema(description = "Срок исполнения", example = "2025-06-01")
    val dueDate: LocalDate? = null,

    @Schema(description = "Кем забронирован (ID пользователя)")
    val reservedBy: UUID? = null,

    @Schema(description = "Когда забронирован")
    val reservedAt: LocalDateTime? = null,

    @Schema(description = "Дата создания")
    val createdAt: LocalDateTime
)
