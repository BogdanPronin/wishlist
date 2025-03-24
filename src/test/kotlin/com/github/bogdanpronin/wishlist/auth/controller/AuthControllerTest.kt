package com.github.bogdanpronin.wishlist.auth.controller

import com.github.bogdanpronin.wishlist.config.IntegrationTest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.bogdanpronin.wishlist.auth.dto.AuthRequest
import com.github.bogdanpronin.wishlist.auth.dto.RegisterRequest
import com.github.bogdanpronin.wishlist.core.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertEquals

class AuthControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository
) : IntegrationTest(){

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `should register a new user`() {
        val request = RegisterRequest(
            email = "newuser@example.com",
            password = "securepassword",
            fullName = "Test User"
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { exists() }
            jsonPath("$.refreshToken") { exists() }
        }

        val savedUser = userRepository.findByEmail("newuser@example.com")
        assertEquals("newuser@example.com", savedUser?.email)
    }

    @Test
    fun `should login with valid credentials`() {
        val request = RegisterRequest("login@example.com", "pass1234", "Login Test")
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }

        val loginRequest = AuthRequest("login@example.com", "pass1234")

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { exists() }
            jsonPath("$.refreshToken") { exists() }
        }
    }

    @Test
    fun `should refresh token`() {
        val request = RegisterRequest("refresh@example.com", "refreshpass", "Refresh User")
        val loginResponse = mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andReturn().response.contentAsString

        val refreshToken = objectMapper.readTree(loginResponse).get("refreshToken").asText()

        mockMvc.post("/api/auth/refresh") {
            contentType = MediaType.APPLICATION_JSON
            content = refreshToken
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { exists() }
            jsonPath("$.refreshToken") { exists() }
        }
    }
}
