package com.github.bogdanpronin.wishlist.auth.config

import com.github.bogdanpronin.wishlist.auth.token.JwtUtil
import com.github.bogdanpronin.wishlist.auth.userdetails.UserPrincipal
import com.github.bogdanpronin.wishlist.core.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val userService: UserService
) {

    @Bean
    fun authenticationManager(http: HttpSecurity, passwordEncoder: PasswordEncoder, userDetailsService: UserDetailsService): AuthenticationManager {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
        return builder.build()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html"
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun jwtFilter() = object : OncePerRequestFilter() {
        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
        ) {
            val authHeader = request.getHeader("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)
                try {
                    val userId = jwtUtil.extractUserIdFromAccess(token)
                    val user = userService.getById(userId)

                    val userDetails = UserPrincipal(user)
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    SecurityContextHolder.getContext().authentication = authentication

                } catch (e: Exception) {
                    if (!response.isCommitted) {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
                    }
                    return
                }
            }

            filterChain.doFilter(request, response)
        }
    }


    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOriginPatterns = listOf("*")
        config.allowedHeaders = listOf("Authorization", "Content-Type")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
