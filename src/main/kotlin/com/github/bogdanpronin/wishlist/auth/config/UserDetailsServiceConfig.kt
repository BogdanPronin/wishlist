package com.github.bogdanpronin.wishlist.auth.config

import com.github.bogdanpronin.wishlist.auth.userdetails.UserPrincipal
import com.github.bogdanpronin.wishlist.core.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService

@Configuration
class UserDetailsServiceConfig(
    private val userService: UserService
) {

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { email ->
            val user = userService.getByEmail(email)
                ?: throw IllegalArgumentException("User not found with email $email")
            UserPrincipal(user)
        }
    }
}
