package com.github.bogdanpronin.wishlist.core.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "wish_lists")
data class WishList(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val title: String,

    val description: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @OneToMany(mappedBy = "wishList", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val wishes: List<Wish> = emptyList()
)
