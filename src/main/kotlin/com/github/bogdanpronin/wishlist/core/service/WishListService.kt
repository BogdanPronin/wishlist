package com.github.bogdanpronin.wishlist.core.service

import com.github.bogdanpronin.wishlist.core.model.WishList
import com.github.bogdanpronin.wishlist.core.repository.WishListRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class WishListService(
    private val wishListRepository: WishListRepository
) {

    private val log = LoggerFactory.getLogger(WishListService::class.java)

    fun getById(id: UUID): WishList {
        log.debug("Запрос списка желаний по ID: $id")
        return wishListRepository.findById(id).orElseThrow {
            log.warn("Список желаний с ID $id не найден")
            NoSuchElementException("WishList not found")
        }
    }

    fun save(wishList: WishList): WishList {
        log.info("Сохранение списка желаний: $wishList")
        return wishListRepository.save(wishList)
    }

    fun getAllByUser(userId: UUID): List<WishList> {
        log.debug("Запрос всех списков пользователя с ID: $userId")
        return wishListRepository.findAllByUserId(userId)
    }
}
