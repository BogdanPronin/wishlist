package com.github.bogdanpronin.wishlist.core.service

import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun getById(id: UUID): User {
        log.debug("Поиск пользователя по ID: $id")
        return userRepository.findById(id).orElseThrow {
            log.warn("Пользователь с ID $id не найден")
            NoSuchElementException("User not found")
        }
    }

    fun getByEmail(email: String): User? {
        log.debug("Поиск пользователя по email: $email")
        val user = userRepository.findByEmail(email)
        if (user == null) {
            log.info("Пользователь с email $email не найден")
        } else {
            log.debug("Пользователь найден: ${user.id}")
        }
        return user
    }

    fun save(user: User): User {
        log.info("Сохранение пользователя: ${user.email}")
        return userRepository.save(user)
    }
}
