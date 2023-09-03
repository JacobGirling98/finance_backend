package unit.fixtures

import dao.Entity
import java.util.*

object Doubles {
    data class TestDomain(
        val name: String = "Jacob",
        val age: Int = 24
    )
}

fun Doubles.TestDomain.asEntity(id: UUID) = Entity(id, this)
