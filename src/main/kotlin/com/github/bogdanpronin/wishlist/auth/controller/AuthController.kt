package com.github.bogdanpronin.wishlist.auth.controller

import com.github.bogdanpronin.wishlist.auth.dto.AuthRequest
import com.github.bogdanpronin.wishlist.auth.dto.AuthResponse
import com.github.bogdanpronin.wishlist.auth.dto.RegisterRequest
import com.github.bogdanpronin.wishlist.auth.service.AuthService
import com.github.bogdanpronin.wishlist.auth.token.JwtUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Регистрация и вход пользователей")
class AuthController(
    private val authService: AuthService,
    private val jwtUtil: JwtUtil
) {

    @Operation(
        summary = "Регистрация пользователя",
        description = "Создание нового пользователя и выдача JWT токена"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
        ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    )
    @PostMapping("/register")

    fun register(@RequestBody request: RegisterRequest): AuthResponse =
        authService.register(request)

    @Operation(
        summary = "Аутентификация",
        description = "Вход пользователя и получение JWT токена"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Успешный вход"),
        ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    )
    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): AuthResponse =
        authService.login(request)

    @PostMapping("/refresh")
    fun refresh(@RequestBody token: String): AuthResponse {
        val userId = jwtUtil.extractUserIdFromRefresh(token)
        return authService.generateTokens(userId)
    }

}
