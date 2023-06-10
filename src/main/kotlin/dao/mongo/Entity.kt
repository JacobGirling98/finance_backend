package dao.mongo

data class Entity<T>(val id: String, val domain: T)
