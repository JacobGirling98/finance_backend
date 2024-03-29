package dao

import java.time.LocalDateTime
import java.util.*

data class Entity<T>(
    val id: UUID,
    val domain: T,
    val lastModified: LocalDateTime
) {
    fun <R> map(fn: (T) -> R): Entity<R> = Entity(id, fn(domain), lastModified)
}

fun <T> entityOf(domain: T, now: () -> LocalDateTime = { LocalDateTime.now() }) =
    Entity(UUID.randomUUID(), domain, now())

fun <T> T.asEntity(id: UUID, now: () -> LocalDateTime = { LocalDateTime.now() }) = Entity(id, this, now())

fun <T> T.asRandomEntity(now: () -> LocalDateTime = { LocalDateTime.now() }) = Entity(UUID.randomUUID(), this, now())
