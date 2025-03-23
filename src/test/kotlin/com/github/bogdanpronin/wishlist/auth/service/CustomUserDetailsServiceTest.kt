package com.github.bogdanpronin.wishlist.auth.service

import com.github.bogdanpronin.wishlist.auth.userdetails.UserPrincipal
import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.service.UserService
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.*

class CustomUserDetailsServiceTest {

    private lateinit var userService: UserService
    private lateinit var customUserDetailsService: CustomUserDetailsService

    private val email = "test@example.com"
    private val password = "password"
    private val user = User(
        id = UUID.randomUUID(),
        email = email,
        password = password,
        fullName = "Test User"
    )

    @BeforeEach
    fun setUp() {
        userService = mock(UserService::class.java)
        customUserDetailsService = CustomUserDetailsService(userService)
    }

    @Test
    fun `should return UserPrincipal when user exists`() {
        `when`(userService.getByEmail(email)).thenReturn(user)

        val result = customUserDetailsService.loadUserByUsername(email)

        Assertions.assertTrue(result is UserPrincipal)
        Assertions.assertEquals(email, result.username)
        verify(userService).getByEmail(email)
    }

    @Test
    fun `should throw UsernameNotFoundException when user not found`() {
        `when`(userService.getByEmail(email)).thenReturn(null)

        val exception = Assertions.assertThrows(UsernameNotFoundException::class.java) {
            customUserDetailsService.loadUserByUsername(email)
        }

        Assertions.assertEquals("Пользователь с email $email не найден", exception.message)
        verify(userService).getByEmail(email)
    }
}
