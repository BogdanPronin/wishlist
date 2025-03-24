package com.github.bogdanpronin.wishlist.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
    @JsonIgnore
    val wishLists: List<WishList> = emptyList(),

    @OneToMany(mappedBy = "reservedBy", fetch = FetchType.LAZY)
    val reservedWishes: List<Wish> = emptyList()
) {
    override fun toString(): String =
        "User(id=$id, email=$email, fullName=$fullName)"
}
