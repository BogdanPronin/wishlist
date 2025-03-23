package com.github.bogdanpronin.wishlist.core.repository

import com.github.bogdanpronin.wishlist.core.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
}
