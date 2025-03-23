package com.github.bogdanpronin.wishlist.core.controller

import com.github.bogdanpronin.wishlist.auth.userdetails.UserPrincipal
import com.github.bogdanpronin.wishlist.core.dto.*
import com.github.bogdanpronin.wishlist.core.mapper.EntityMapper
import com.github.bogdanpronin.wishlist.core.service.WishListService
import com.github.bogdanpronin.wishlist.core.service.WishService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/wishes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Подарки", description = "CRUD и бронирование подарков")
class WishController(
    private val wishService: WishService,
    private val wishListService: WishListService
) {
    @Operation(summary = "Получить все подарки из списка")
    @GetMapping("/list/{wishListId}")
    fun getByWishList(
        @PathVariable wishListId: UUID
    ): List<WishResponseDto> =
        wishService.getByWishList(wishListId).map(EntityMapper::toWishDto)

    @Operation(summary = "Забронировать подарок")
    @PostMapping("/{id}/reserve")
    fun reserveWish(
        @PathVariable id: UUID,
        @AuthenticationPrincipal reserver: UserPrincipal
    ): WishResponseDto {
        return EntityMapper.toWishDto(wishService.reserveWish(id, reserver.getUser()))
    }

    @Operation(summary = "Снять бронь с подарка")
    @DeleteMapping("/{id}/reserve")
    fun cancelReservation(
        @PathVariable id: UUID,
        @AuthenticationPrincipal requester: UserPrincipal
    ): WishResponseDto {
        return EntityMapper.toWishDto(wishService.cancelReservation(id, requester.getUser()))
    }

    @PostMapping
    @Operation(summary = "Создать подарок")
    fun createWish(
        @RequestBody dto: WishCreateDto,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<Any> {
        val wishList = wishListService.getById(dto.wishListId)

        if (wishList.user.id != user.getId()) {
            return ResponseEntity.status(403).body(mapOf("error" to "Вы не владелец этого списка"))
        }

        val wish = EntityMapper.dtoToWish(dto, wishList)
        val saved = wishService.save(wish)
        return ResponseEntity.ok(EntityMapper.toWishDto(saved))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить подарок")
    fun updateWish(
        @PathVariable id: UUID,
        @RequestBody dto: WishUpdateDto,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<WishResponseDto> {
        val existing = wishService.getById(id)

        if (existing.wishList.user.id != user.getId()) {
            return ResponseEntity.status(403).build()
        }

        val updated = EntityMapper.updateWishFromDto(existing, dto)
        val saved = wishService.update(updated)
        return ResponseEntity.ok(EntityMapper.toWishDto(saved))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить подарок (если не забронирован)")
    fun deleteWish(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: UserPrincipal
    ) {
        wishService.deleteWish(id, user.getUser())
    }
}
