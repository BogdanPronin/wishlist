package com.github.bogdanpronin.wishlist.core.service

import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.model.Wish
import com.github.bogdanpronin.wishlist.core.model.WishList
import com.github.bogdanpronin.wishlist.core.repository.WishRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*

class WishServiceTest {
    private val wishRepository: WishRepository = mock(WishRepository::class.java)
    private val wishService = WishService(wishRepository)

    private val user = User(id = UUID.randomUUID(), email = "test@test.com", password = "123")
    private val otherUser = User(id = UUID.randomUUID(), email = "other@test.com", password = "456")
    private val wishList = WishList(id = UUID.randomUUID(), title = "На День Рождения", user = user)
    private val wish = Wish(id = UUID.randomUUID(), title = "Gift", wishList = wishList)

    @Test
    fun `getByWishList should return list of wishes`() {
        val list = listOf(wish)
        `when`(wishRepository.findAllByWishListId(wishList.id)).thenReturn(list)

        val result = wishService.getByWishList(wishList.id)
        assertEquals(1, result.size)
        assertEquals(wish, result[0])
    }

    @Test
    fun `getById should return wish when found`() {
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(wish))
        val result = wishService.getById(wish.id)
        assertEquals(wish, result)
    }

    @Test
    fun `getById should throw exception when not found`() {
        val id = UUID.randomUUID()
        `when`(wishRepository.findById(id)).thenReturn(Optional.empty())
        assertThrows<NoSuchElementException> { wishService.getById(id) }
    }

    @Test
    fun `save should return saved wish`() {
        `when`(wishRepository.save(wish)).thenReturn(wish)
        val result = wishService.save(wish)
        assertEquals(wish, result)
    }

    @Test
    fun `update should return updated wish`() {
        `when`(wishRepository.save(wish)).thenReturn(wish)
        val result = wishService.update(wish)
        assertEquals(wish, result)
    }

    @Test
    fun `reserveWish should set reservedBy and reservedAt`() {
        val unreservedWish = wish.copy(reservedBy = null)
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(unreservedWish))
        `when`(wishRepository.save(any(Wish::class.java))).thenAnswer { it.getArgument(0) }

        val result = wishService.reserveWish(wish.id, otherUser)
        assertEquals(otherUser, result.reservedBy)
        assertNotNull(result.reservedAt)
    }

    @Test
    fun `reserveWish should fail if already reserved`() {
        val reservedWish = wish.copy(reservedBy = otherUser)
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(reservedWish))

        assertThrows<IllegalStateException> {
            wishService.reserveWish(wish.id, user)
        }
    }

    @Test
    fun `reserveWish should fail if user is owner`() {
        val unreservedWish = wish.copy(reservedBy = null)
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(unreservedWish))

        assertThrows<IllegalStateException> {
            wishService.reserveWish(wish.id, user)
        }
    }

    @Test
    fun `cancelReservation should remove reserver`() {
        val reservedWish = wish.copy(reservedBy = otherUser, reservedAt = LocalDateTime.now())
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(reservedWish))
        `when`(wishRepository.save(any(Wish::class.java))).thenAnswer { it.getArgument(0) }

        val result = wishService.cancelReservation(wish.id, otherUser)
        assertNull(result.reservedBy)
        assertNull(result.reservedAt)
    }

    @Test
    fun `cancelReservation should fail if not reserver`() {
        val reservedWish = wish.copy(reservedBy = otherUser)
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(reservedWish))

        assertThrows<IllegalStateException> {
            wishService.cancelReservation(wish.id, user)
        }
    }

    @Test
    fun `deleteWish should delete if not reserved and owned`() {
        val deletableWish = wish.copy(reservedBy = null)
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(deletableWish))

        wishService.deleteWish(wish.id, user)
        verify(wishRepository).delete(deletableWish)
    }

    @Test
    fun `deleteWish should fail if not owner`() {
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(wish))

        assertThrows<SecurityException> {
            wishService.deleteWish(wish.id, otherUser)
        }
    }

    @Test
    fun `deleteWish should fail if reserved`() {
        val reservedWish = wish.copy(reservedBy = otherUser)
        `when`(wishRepository.findById(wish.id)).thenReturn(Optional.of(reservedWish))

        assertThrows<IllegalStateException> {
            wishService.deleteWish(wish.id, user)
        }
    }
}