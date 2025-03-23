package com.github.bogdanpronin.wishlist.core.service

import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class UserServiceTest {

    private val userRepository = mock(UserRepository::class.java)
    private val userService = UserService(userRepository)

    private val userId = UUID.randomUUID()
    private val email = "test@example.com"
    private val user = User(
        id = userId,
        email = email,
        password = "hashedPassword",
        fullName = "Test User"
    )

    @Test
    fun `getById should return user when found`() {
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(user))

        val result = userService.getById(userId)

        assertEquals(user, result)
        verify(userRepository).findById(userId)
    }

    @Test
    fun `getById should throw exception when user not found`() {
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            userService.getById(userId)
        }

        verify(userRepository).findById(userId)
    }

    @Test
    fun `getByEmail should return user when found`() {
        `when`(userRepository.findByEmail(email)).thenReturn(user)

        val result = userService.getByEmail(email)

        assertEquals(user, result)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `getByEmail should return null when user not found`() {
        `when`(userRepository.findByEmail(email)).thenReturn(null)

        val result = userService.getByEmail(email)

        assertNull(result)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `save should return saved user`() {
        `when`(userRepository.save(user)).thenReturn(user)

        val result = userService.save(user)

        assertEquals(user, result)
        verify(userRepository).save(user)
    }
}
