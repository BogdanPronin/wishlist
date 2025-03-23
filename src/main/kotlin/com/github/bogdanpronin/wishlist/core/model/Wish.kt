package com.github.bogdanpronin.wishlist.core.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "wishes")
data class Wish(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val title: String,

    val description: String? = null,

    val priority: Int? = null,

    @Column(name = "due_date")
    val dueDate: LocalDate? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "wish_list_id")
    val wishList: WishList,

    @ManyToOne
    @JoinColumn(name = "reserved_by_user_id")
    val reservedBy: User? = null,

    @Column(name = "reserved_at")
    val reservedAt: LocalDateTime? = null
)
