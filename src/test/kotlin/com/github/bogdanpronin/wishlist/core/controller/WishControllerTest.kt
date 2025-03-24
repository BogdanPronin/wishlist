package com.github.bogdanpronin.wishlist.core.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.bogdanpronin.wishlist.auth.dto.AuthResponse
import com.github.bogdanpronin.wishlist.config.IntegrationTest
import com.github.bogdanpronin.wishlist.core.dto.WishCreateDto
import com.github.bogdanpronin.wishlist.core.dto.WishUpdateDto
import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.model.Wish
import com.github.bogdanpronin.wishlist.core.model.WishList
import com.github.bogdanpronin.wishlist.core.repository.UserRepository
import com.github.bogdanpronin.wishlist.core.repository.WishListRepository
import com.github.bogdanpronin.wishlist.core.repository.WishRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.delete
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@SpringBootTest
@AutoConfigureMockMvc
class WishControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository,
    val wishListRepository: WishListRepository,
    val wishRepository: WishRepository,
    val passwordEncoder: PasswordEncoder
) : IntegrationTest() {

    lateinit var user: User
    lateinit var token: String
    lateinit var wishList: WishList

    @BeforeEach
    fun setUp() {
        wishRepository.deleteAll()
        wishListRepository.deleteAll()
        userRepository.deleteAll()

        user = userRepository.save(
            User(
                email = "test@example.com",
                password = passwordEncoder.encode("password")
            )
        )

        wishList = wishListRepository.save(
            WishList(
                title = "Birthday",
                user = user
            )
        )

        val loginResponse = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                mapOf("email" to user.email, "password" to "password")
            )
        }.andReturn().response.contentAsString

        token = objectMapper.readValue(loginResponse, AuthResponse::class.java).accessToken
    }

    @Test
    fun `should create wish`() {
        val dto = WishCreateDto(
            title = "New Laptop",
            description = "Gaming laptop",
            priority = 1,
            dueDate = null,
            wishListId = wishList.id
        )

        mockMvc.post("/api/wishes") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(dto)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("New Laptop") }
        }
    }

    @Test
    fun `should get wishes by list`() {
        val wish = wishRepository.save(
            Wish(
                title = "Camera",
                description = "DSLR",
                wishList = wishList
            )
        )

        mockMvc.get("/api/wishes/list/${wishList.id}") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$[0].title") { value("Camera") }
        }
    }

    @Test
    fun `should update wish`() {
        val wish = wishRepository.save(
            Wish(
                title = "Tablet",
                description = "Old model",
                wishList = wishList
            )
        )

        val updateDto = WishUpdateDto(
            title = "Updated Tablet",
            description = "New model",
            priority = 2,
            dueDate = null
        )

        mockMvc.put("/api/wishes/${wish.id}") {
            header("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updateDto)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("Updated Tablet") }
        }
    }

    @Test
    fun `should delete wish`() {
        val wish = wishRepository.save(
            Wish(
                title = "Delete Me",
                wishList = wishList
            )
        )

        mockMvc.delete("/api/wishes/${wish.id}") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
        }
    }
}
