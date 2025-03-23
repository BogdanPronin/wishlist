package com.github.bogdanpronin.wishlist.core.repository

import com.github.bogdanpronin.wishlist.core.model.Wish
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WishRepository : JpaRepository<Wish, UUID> {

    fun findAllByWishListId(wishListId: UUID): List<Wish>

    fun findAllByReservedById(userId: UUID): List<Wish>
}
