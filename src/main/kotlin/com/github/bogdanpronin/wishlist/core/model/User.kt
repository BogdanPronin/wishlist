package com.github.bogdanpronin.wishlist.core.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "full_name")
    val fullName: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val wishLists: List<WishList> = emptyList(),

    @OneToMany(mappedBy = "reservedBy", fetch = FetchType.LAZY)
    val reservedWishes: List<Wish> = emptyList()
)