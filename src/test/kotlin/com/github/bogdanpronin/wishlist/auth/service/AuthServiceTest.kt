package com.github.bogdanpronin.wishlist.auth.service

import com.github.bogdanpronin.wishlist.auth.dto.AuthRequest
import com.github.bogdanpronin.wishlist.auth.dto.RegisterRequest
import com.github.bogdanpronin.wishlist.auth.exception.EmailAlreadyExistsException
import com.github.bogdanpronin.wishlist.auth.token.JwtUtil
import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.service.UserService
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthServiceTest {

    private lateinit var userService: UserService
    private lateinit var jwtUtil: JwtUtil
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var authService: AuthService

    private val email = "test@example.com"
    private val password = "password"
    private val encodedPassword = "encodedPassword"
    private val fullName = "Test User"
    private val userId = UUID.randomUUID()

    private val user = User(
        id = userId,
        email = email,
        password = encodedPassword,
        fullName = fullName
    )

    @BeforeEach
    fun setUp() {
        userService = mock(UserService::class.java)
        jwtUtil = mock(JwtUtil::class.java)
        passwordEncoder = mock(PasswordEncoder::class.java)
        authService = AuthService(userService, jwtUtil, passwordEncoder)
    }

    @Test
    fun `register should succeed`() {
        val request = RegisterRequest(email, password, fullName)

        `when`(userService.getByEmail(email)).thenReturn(null)
        `when`(passwordEncoder.encode(password)).thenReturn(encodedPassword)
        `when`(userService.save(any<User>())).thenReturn(user)
        `when`(jwtUtil.generateAccessToken(userId)).thenReturn("access-token")
        `when`(jwtUtil.generateRefreshToken(userId)).thenReturn("refresh-token")

        val response = authService.register(request)

        Assertions.assertEquals("access-token", response.accessToken)
        Assertions.assertEquals("refresh-token", response.refreshToken)
    }

    @Test
    fun `register should throw when email already exists`() {
        val request = RegisterRequest(email, password, fullName)

        `when`(userService.getByEmail(email)).thenReturn(user)

        val exception = Assertions.assertThrows(EmailAlreadyExistsException::class.java) {
            authService.register(request)
        }

        Assertions.assertEquals("Пользователь с таким email уже существует", exception.message)
        verify(userService, never()).save(any<User>())
    }

    @Test
    fun `login should succeed with correct credentials`() {
        val request = AuthRequest(email, password)

        `when`(userService.getByEmail(email)).thenReturn(user)
        `when`(passwordEncoder.matches(password, encodedPassword)).thenReturn(true)
        `when`(jwtUtil.generateAccessToken(userId)).thenReturn("access-token")
        `when`(jwtUtil.generateRefreshToken(userId)).thenReturn("refresh-token")

        val response = authService.login(request)

        Assertions.assertEquals("access-token", response.accessToken)
        Assertions.assertEquals("refresh-token", response.refreshToken)
    }

    @Test
    fun `login should throw if user not found`() {
        val request = AuthRequest(email, password)

        `when`(userService.getByEmail(email)).thenReturn(null)

        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            authService.login(request)
        }

        Assertions.assertEquals("Пользователь не найден", exception.message)
    }

    @Test
    fun `login should throw if password is incorrect`() {
        val request = AuthRequest(email, password)

        `when`(userService.getByEmail(email)).thenReturn(user)
        `when`(passwordEncoder.matches(password, encodedPassword)).thenReturn(false)

        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            authService.login(request)
        }

        Assertions.assertEquals("Неверный пароль", exception.message)
    }

    private inline fun <reified T> any(): T = Mockito.any(T::class.java)
}
