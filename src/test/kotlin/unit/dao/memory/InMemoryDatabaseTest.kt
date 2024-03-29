package unit.dao.memory

import dao.Entity
import dao.memory.InMemoryDatabase
import exceptions.NotFoundException
import helpers.fixtures.Doubles.TestDomain
import helpers.fixtures.asEntity
import helpers.matchers.shouldContainDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime
import java.util.*

class InMemoryDatabaseTest : FunSpec({

    val now = LocalDateTime.now()
    val database = InMemoryDatabase<TestDomain>(now = { now })

    test("can save a domain to the database and select it") {
        database.save(TestDomain())

        database.selectAll() shouldContainDomain TestDomain()
    }

    test("can save multiple domain objects") {
        database.save(TestDomain(), TestDomain(name = "Jake"))

        with(database.selectAll()) {
            this shouldContainDomain TestDomain()
            this shouldContainDomain TestDomain(name = "Jake")
        }
    }

    test("can find a saved entity by its id") {
        val id = database.save(TestDomain())

        database.findById(id) shouldBe TestDomain().asEntity(id) { now }
    }

    test("finding an id that does not exist returns null") {
        database.findById(UUID.randomUUID()) shouldBe null
    }

    test("can update an entity") {
        val entity = database.findById(database.save(TestDomain()))!!
        val newDomain = TestDomain("Jake", 25)

        database.update(Entity(entity.id, newDomain, now)) shouldBe null
        database.findById(entity.id) shouldBe newDomain.asEntity(entity.id) { now }
    }

    test("updating an entity that doesn't exist returns a NotFoundException") {
        val id = UUID.randomUUID()
        database.update(Entity(id, TestDomain(), now)) shouldBe NotFoundException(id)
    }

    test("can delete an entity") {
        val id = database.save(TestDomain())
        database.findById(id) shouldNotBe null

        database.delete(id)

        database.findById(id) shouldBe null
    }

    test("deleting an entity that doesn't exist returns a NotFoundException") {
        val id = UUID.randomUUID()
        database.delete(id) shouldBe NotFoundException(id)
    }

    test("can delete all entities") {
        database.save(TestDomain(), TestDomain(), TestDomain())

        database.selectAll() shouldHaveSize 3

        database.deleteAll()

        database.selectAll() shouldHaveSize 0
    }
})
