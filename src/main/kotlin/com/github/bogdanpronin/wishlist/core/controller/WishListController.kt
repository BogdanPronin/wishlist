package com.github.bogdanpronin.wishlist.core.controller

import com.github.bogdanpronin.wishlist.auth.userdetails.UserPrincipal
import com.github.bogdanpronin.wishlist.core.dto.WishListCreateDto
import com.github.bogdanpronin.wishlist.core.dto.WishListDto
import com.github.bogdanpronin.wishlist.core.mapper.EntityMapper
import com.github.bogdanpronin.wishlist.core.service.WishListService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/wishlists")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Списки желаний", description = "CRUD для списков подарков")
class WishListController(
    private val wishListService: WishListService
) {

    @PostMapping
    @Operation(summary = "Создать список желаний")
    fun createWishList(
        @RequestBody dto: WishListCreateDto,
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<WishListDto> {
        val saved = wishListService.save(EntityMapper.dtoToWishList(dto, user.getUser()))
        return ResponseEntity.ok(EntityMapper.toWishListDto(saved))
    }

    @GetMapping
    @Operation(summary = "Получить все списки пользователя")
    fun getAllWishLists(
        @AuthenticationPrincipal user: UserPrincipal
    ): ResponseEntity<List<WishListDto>> {
        val lists = wishListService.getAllByUser(user.getId())
        return ResponseEntity.ok(lists.map(EntityMapper::toWishListDto))
    }
}
