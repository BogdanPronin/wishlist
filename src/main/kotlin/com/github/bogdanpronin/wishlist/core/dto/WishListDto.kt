package com.github.bogdanpronin.wishlist.core.dto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "Список желаний пользователя")
data class WishListDto(

    @Schema(description = "ID списка", example = "13ec6f35-91ce-4a9d-b3a7-bc497c6a301d")
    val id: UUID,

    @Schema(description = "Название списка", example = "День рождения 2025")
    val title: String,

    @Schema(description = "Описание списка", example = "Подарки, которые я хочу получить на день рождения")
    val description: String? = null,

    @Schema(description = "Дата создания", example = "2025-03-22T12:00:00")
    val createdAt: LocalDateTime
)
