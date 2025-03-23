package com.github.bogdanpronin.wishlist.core.dto

import io.swagger.v3.oas.annotations.media.Schema
@Schema(description = "Список желаний пользователя")
data class WishListCreateDto(

    @Schema(description = "Название списка", example = "День рождения 2025")
    val title: String,

    @Schema(description = "Описание списка", example = "Подарки, которые я хочу получить на день рождения")
    val description: String? = null,

)