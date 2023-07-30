package dao

import java.util.*

data class Entity<T>(val id: UUID, val domain: T)

fun <T> entityOf(domain: T) = Entity(UUID.randomUUID(), domain)
fun <T> T.asEntity(id: UUID) = Entity(id, this)