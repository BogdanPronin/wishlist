package com.github.bogdanpronin.wishlist.auth.service

import com.github.bogdanpronin.wishlist.auth.userdetails.UserPrincipal
import com.github.bogdanpronin.wishlist.core.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userService: UserService
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        return userService.getByEmail(email)?.let { UserPrincipal(it) }
            ?: throw UsernameNotFoundException("Пользователь с email $email не найден")
    }
}
