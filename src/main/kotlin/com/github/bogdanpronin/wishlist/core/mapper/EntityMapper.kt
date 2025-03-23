package com.github.bogdanpronin.wishlist.core.mapper

import com.github.bogdanpronin.wishlist.core.dto.*
import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.model.Wish
import com.github.bogdanpronin.wishlist.core.model.WishList

object EntityMapper {

    fun toUserDto(user: User) = UserDto(
        id = user.id,
        email = user.email,
        fullName = user.fullName
    )

    fun toWishListDto(wishList: WishList) = WishListDto(
        id = wishList.id,
        title = wishList.title,
        description = wishList.description,
        createdAt = wishList.createdAt
    )

    fun toWishDto(wish: Wish) = WishResponseDto(
        id = wish.id,
        title = wish.title,
        description = wish.description,
        priority = wish.priority,
        dueDate = wish.dueDate,
        reservedBy = wish.reservedBy?.id,
        reservedAt = wish.reservedAt,
        createdAt = wish.createdAt
    )

    fun dtoToWishList(dto: WishListCreateDto, user: User): WishList {
        return WishList(
            title = dto.title,
            description = dto.description,
            user = user
        )
    }

    fun dtoToWish(dto: WishCreateDto, wishList: WishList): Wish {
        return Wish(
            title = dto.title,
            description = dto.description,
            priority = dto.priority,
            dueDate = dto.dueDate,
            wishList = wishList
        )
    }

    fun updateWishFromDto(existing: Wish, dto: WishUpdateDto): Wish {
        return existing.copy(
            title = dto.title ?: existing.title,
            description = dto.description ?: existing.description,
            priority = dto.priority ?: existing.priority,
            dueDate = dto.dueDate ?: existing.dueDate
        )
    }


}
