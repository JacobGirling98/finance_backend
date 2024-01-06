package helpers.matchers

import dao.Entity
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

infix fun <T : Any> List<Entity<T>>.shouldContainDomain(domain: T) = map { it.domain } shouldContain domain
infix fun <T : Any> List<Entity<T>>.shouldNotContainDomain(domain: T) = map { it.domain } shouldNotContain domain
