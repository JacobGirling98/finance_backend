package unit.fixtures

import dao.Entity
import java.util.*

data class TestDomain(
    val name: String = "Jacob",
    val age: Int = 24
)

fun TestDomain.asEntity(id: UUID) = Entity(id, this)