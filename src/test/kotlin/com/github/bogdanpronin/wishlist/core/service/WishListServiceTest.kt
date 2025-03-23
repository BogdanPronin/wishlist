package com.github.bogdanpronin.wishlist.core.service

import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.model.WishList
import com.github.bogdanpronin.wishlist.core.repository.WishListRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class WishListServiceTest {

    private val wishListRepository = mock(WishListRepository::class.java)
    private val service = WishListService(wishListRepository)

    @Test
    fun `getById should return wish list when found`() {
        val id = UUID.randomUUID()
        val expected = WishList(
            id = id,
            user = User(
                UUID.randomUUID(),
                email = "test@test.com",
                password = "hashed"
            ),
            title = "Birthday"
        )
        `when`(wishListRepository.findById(id)).thenReturn(Optional.of(expected))

        val actual = service.getById(id)

        assertEquals(expected, actual)
    }

    @Test
    fun `getById should throw when not found`() {
        val id = UUID.randomUUID()
        `when`(wishListRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            service.getById(id)
        }
    }

    @Test
    fun `save should call repository and return saved wishlist`() {
        val wishList = WishList(
            id = UUID.randomUUID(),
            user = User(
                UUID.randomUUID(),
                email = "user@test.com",
                password = "hashed"
            ),
            title = "New Year"
        )
        `when`(wishListRepository.save(wishList)).thenReturn(wishList)

        val result = service.save(wishList)

        assertEquals(wishList, result)
        verify(wishListRepository).save(wishList)
    }

    @Test
    fun `getAllByUser should return list of wishlists`() {
        val userId = UUID.randomUUID()
        val list = listOf(
            WishList(
                UUID.randomUUID(),
                user = User(
                    userId,
                    "u@mail.com",
                    "pass"
                ),
                title = "List1"),
            WishList(
                UUID.randomUUID(),
                user = User(
                    userId,
                    "u@mail.com",
                    "pass"
                ),
                title = "List2"),
        )
        `when`(wishListRepository.findAllByUserId(userId)).thenReturn(list)

        val result = service.getAllByUser(userId)

        assertEquals(list, result)
    }
}