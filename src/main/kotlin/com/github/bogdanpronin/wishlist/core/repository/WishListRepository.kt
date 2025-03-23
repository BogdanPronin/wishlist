package com.github.bogdanpronin.wishlist.core.repository

import com.github.bogdanpronin.wishlist.core.model.WishList
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WishListRepository : JpaRepository<WishList, UUID> {
    fun findAllByUserId(userId: UUID): List<WishList>
}
