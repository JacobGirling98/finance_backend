package dao

import java.time.LocalDateTime
import java.util.*

open class Entity<T>(
    val id: UUID,
    val domain: T
) {
    open fun <R> map(fn: (T) -> R): Entity<R> = Entity(id, fn(domain))

    open fun copy(
        id: UUID = this.id,
        domain: T = this.domain
    ) = Entity(id, domain)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Entity<*>

        if (id != other.id) return false
        if (domain != other.domain) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (domain?.hashCode() ?: 0)
        return result
    }


}

fun <T> T.asRandomEntity(now: () -> LocalDateTime = { LocalDateTime.now() }) = Entity(UUID.randomUUID(), this)

fun <T> T.asEntity(id: UUID) = Entity(id, this)