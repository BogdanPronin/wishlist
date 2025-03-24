package com.github.bogdanpronin.wishlist.core.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.bogdanpronin.wishlist.auth.dto.AuthResponse
import com.github.bogdanpronin.wishlist.config.IntegrationTest
import com.github.bogdanpronin.wishlist.core.dto.WishListCreateDto
import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.model.WishList
import com.github.bogdanpronin.wishlist.core.repository.UserRepository
import com.github.bogdanpronin.wishlist.core.repository.WishListRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class WishListControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository,
    val wishListRepository: WishListRepository,
    val passwordEncoder: PasswordEncoder
) : IntegrationTest(){

    lateinit var token: String
    lateinit var user: User

    @BeforeEach
    fun setup() {
        wishListRepository.deleteAll()
        userRepository.deleteAll()

        user = userRepository.save(
            User(
                email = "wishlist_user@example.com",
                password = passwordEncoder.encode("password")
            )
        )

        val response = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("email" to user.email, "password" to "password")
            )
        }.andReturn().response.contentAsString

        token = objectMapper.readValue(response, AuthResponse::class.java).accessToken
    }

    @Test
    fun `should create wishlist`() {
        val dto = WishListCreateDto(title = "New Year Gifts")

        mockMvc.post("/api/wishlists") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("New Year Gifts") }
        }
    }

    @Test
    fun `should return all user wishlists`() {
        wishListRepository.save(
            WishList(title = "Birthday", user = user)
        )
        wishListRepository.save(
            WishList(title = "Wedding", user = user)
        )

        mockMvc.get("/api/wishlists") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$.size()") { value(2) }
        }
    }
}
