package com.github.bogdanpronin.wishlist.core.service

import com.github.bogdanpronin.wishlist.core.model.User
import com.github.bogdanpronin.wishlist.core.model.Wish
import com.github.bogdanpronin.wishlist.core.repository.WishRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class WishService(private val wishRepository: WishRepository) {

    private val log = LoggerFactory.getLogger(WishService::class.java)

    fun getByWishList(wishListId: UUID): List<Wish> {
        log.debug("Запрос всех подарков из списка с ID: $wishListId")
        return wishRepository.findAllByWishListId(wishListId)
    }

    fun getById(id: UUID): Wish {
        log.debug("Запрос подарка по ID: $id")
        return wishRepository.findById(id).orElseThrow {
            log.warn("Подарок с ID $id не найден")
            NoSuchElementException("Подарок с ID $id не найден")
        }
    }

    fun update(wish: Wish): Wish {
        log.info("Обновление подарка с ID: ${wish.id}")
        return wishRepository.save(wish)
    }

    fun save(wish: Wish): Wish {
        log.info("Создание нового подарка: $wish")
        return wishRepository.save(wish)
    }

    fun reserveWish(wishId: UUID, reserver: User): Wish {
        log.info("Попытка забронировать подарок ID: $wishId пользователем: ${reserver.id}")
        val wish = getById(wishId)

        if (wish.wishList.user.id == reserver.id) {
            log.warn("Пользователь ${reserver.id} попытался забронировать свой подарок")
            throw IllegalStateException("Нельзя бронировать свой подарок")
        }

        if (wish.reservedBy != null) {
            log.warn("Подарок $wishId уже забронирован пользователем ${wish.reservedBy?.id}")
            throw IllegalStateException("Подарок уже забронирован")
        }

        val reserved = wish.copy(
            reservedBy = reserver,
            reservedAt = LocalDateTime.now()
        )
        log.info("Подарок $wishId успешно забронирован пользователем ${reserver.id}")
        return wishRepository.save(reserved)
    }

    fun cancelReservation(wishId: UUID, requester: User): Wish {
        log.info("Попытка снять бронь с подарка $wishId пользователем: ${requester.id}")
        val wish = getById(wishId)

        if (wish.reservedBy?.id != requester.id) {
            log.warn("Пользователь ${requester.id} не является резервирующим подарок $wishId")
            throw IllegalStateException("Ты не бронировал этот подарок")
        }

        val unreserved = wish.copy(
            reservedBy = null,
            reservedAt = null
        )
        log.info("Бронь с подарка $wishId снята пользователем ${requester.id}")
        return wishRepository.save(unreserved)
    }

    fun deleteWish(id: UUID, requester: User) {
        log.info("Попытка удалить подарок $id пользователем: ${requester.id}")
        val wish = getById(id)

        if (wish.wishList.user.id != requester.id) {
            log.warn("Пользователь ${requester.id} не является владельцем подарка $id")
            throw SecurityException("Вы не являетесь владельцем подарка")
        }

        if (wish.reservedBy != null) {
            log.warn("Попытка удалить забронированный подарок $id")
            throw IllegalStateException("Невозможно удалить забронированный подарок")
        }

        wishRepository.delete(wish)
        log.info("Подарок $id успешно удалён")
    }
}
