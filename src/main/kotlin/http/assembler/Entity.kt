package http.assembler

import dao.Entity

fun <T, R> Entity<T>.map(fn: (T) -> R): Entity<R> = Entity(this.id, fn(domain))
