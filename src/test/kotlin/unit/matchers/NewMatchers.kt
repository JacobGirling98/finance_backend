package unit.matchers

import io.kotest.matchers.collections.shouldContain
import dao.Entity

infix fun <T : Any> List<Entity<T>>.shouldContainDomain(domain: T) = map { it.domain } shouldContain domain