package dao

import java.time.LocalDateTime
import java.util.*

class AuditableEntity<T>(
    id: UUID,
    domain: T,
    override val lastModified: LocalDateTime
) : Entity<T>(id, domain), Auditable {
    fun copy(id: UUID, domain: T, lastModified: LocalDateTime): AuditableEntity<T> {
        return AuditableEntity(id, domain, lastModified)
    }
}

fun <T> Entity<T>.asOf(lastModified: () -> LocalDateTime = { LocalDateTime.now() }) =
    AuditableEntity(id, domain, lastModified())

fun <T> entityOf(domain: T, now: () -> LocalDateTime = { LocalDateTime.now() }) =
    AuditableEntity(UUID.randomUUID(), domain, now())

fun <T> T.asAuditableEntity(id: UUID, now: () -> LocalDateTime = { LocalDateTime.now() }) = AuditableEntity(id, this, now())