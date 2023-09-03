package unit.matchers

import dao.Entity
import io.kotest.matchers.collections.shouldContain

infix fun <T : Any> List<Entity<T>>.shouldContainDomain(domain: T) = map { it.domain } shouldContain domain
