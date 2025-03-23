package com.github.bogdanpronin.wishlist.auth.service

import com.github.bogdanpronin.wishlist.auth.dto.AuthRequest
import com.github.bogdanpronin.wishlist.auth.dto.AuthResponse
import com.github.bogdanpronin.wishlist.auth.dto.RegisterRequest
import com.github.bogdanpronin.wishlist.auth.exception.EmailAlreadyExistsException
import com.github.bogdanpronin.wishlist.auth.token.JwtUtil
import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(AuthService::class.java)

    fun register(request: RegisterRequest): AuthResponse {
        log.info("Попытка регистрации пользователя с email: ${request.email}")

        if (userService.getByEmail(request.email) != null) {
            log.warn("Регистрация не удалась: пользователь с email ${request.email} уже существует")
            throw EmailAlreadyExistsException("Пользователь с таким email уже существует")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            fullName = request.fullName
        )

        val saved = userService.save(user)
        log.info("Пользователь успешно зарегистрирован: ${saved.id}")

        return generateTokens(saved.id)
    }

    fun login(request: AuthRequest): AuthResponse {
        log.info("Попытка входа пользователя с email: ${request.email}")

        val user = userService.getByEmail(request.email)
            ?: run {
                log.warn("Вход не удался: пользователь с email ${request.email} не найден")
                throw IllegalArgumentException("Пользователь не найден")
            }

        if (!passwordEncoder.matches(request.password, user.password)) {
            log.warn("Вход не удался: неверный пароль для пользователя с email ${request.email}")
            throw IllegalArgumentException("Неверный пароль")
        }

        log.info("Пользователь успешно вошел: ${user.id}")
        return generateTokens(user.id)
    }

    fun generateTokens(userId: UUID): AuthResponse {
        log.debug("Генерация токенов для пользователя: $userId")
        return AuthResponse(
            accessToken = jwtUtil.generateAccessToken(userId),
            refreshToken = jwtUtil.generateRefreshToken(userId)
        )
    }
}
