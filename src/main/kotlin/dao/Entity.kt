package dao

import java.time.LocalDateTime
import java.util.*

open class Entity<T>(
    val id: UUID,
    val domain: T,
    val lastModified: LocalDateTime
) {
    fun <R> map(fn: (T) -> R): Entity<R> = Entity(id, fn(domain), lastModified)

    fun copy(
        id: UUID = this.id,
        domain: T = this.domain,
        lastModified: LocalDateTime = this.lastModified
    ) = Entity(id, domain, lastModified)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity<*>

        if (id != other.id) return false
        if (domain != other.domain) return false
        if (lastModified != other.lastModified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (domain?.hashCode() ?: 0)
        result = 31 * result + lastModified.hashCode()
        return result
    }


}

fun <T> entityOf(domain: T, now: () -> LocalDateTime = { LocalDateTime.now() }) =
    Entity(UUID.randomUUID(), domain, now())

fun <T> T.asEntity(id: UUID, now: () -> LocalDateTime = { LocalDateTime.now() }) = Entity(id, this, now())

fun <T> T.asRandomEntity(now: () -> LocalDateTime = { LocalDateTime.now() }) = Entity(UUID.randomUUID(), this, now())
