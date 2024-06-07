package helpers.fixtures

import dao.AuditableEntity
import java.time.LocalDateTime
import java.util.*

object Doubles {
    data class TestDomain(
        val name: String = "Jacob",
        val age: Int = 24
    ) : Comparable<TestDomain> {
        override fun compareTo(other: TestDomain): Int {
            return name.compareTo(other.name)
        }
    }
}

fun Doubles.TestDomain.asEntity(id: UUID, now: () -> LocalDateTime = { LocalDateTime.now() }) = AuditableEntity(id, this, now())
