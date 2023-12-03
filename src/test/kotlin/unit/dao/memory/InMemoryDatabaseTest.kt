package unit.dao.memory

import dao.Entity
import dao.memory.InMemoryDatabase
import domain.PageNumber
import domain.PageSize
import exceptions.NotFoundException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import unit.fixtures.Doubles.TestDomain
import unit.fixtures.asEntity
import unit.matchers.shouldContainDomain
import java.util.*

class InMemoryDatabaseTest : FunSpec({

    val database = InMemoryDatabase<TestDomain>()

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

        database.findById(id) shouldBe TestDomain().asEntity(id)
    }

    test("finding an id that does not exist returns null") {
        database.findById(UUID.randomUUID()) shouldBe null
    }

    test("can update an entity") {
        val entity = database.findById(database.save(TestDomain()))!!
        val newDomain = TestDomain("Jake", 25)

        database.update(Entity(entity.id, newDomain)) shouldBe null
        database.findById(entity.id) shouldBe newDomain.asEntity(entity.id)
    }

    test("updating an entity that doesn't exist returns a NotFoundException") {
        val id = UUID.randomUUID()
        database.update(Entity(id, TestDomain())) shouldBe NotFoundException(id)
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

    context("pagination") {
        test("can get a subset of the data") {
            database.save((0..20).map { TestDomain("Jacob", it) })

            val page = database.selectAll(PageNumber(1), PageSize(5))

            page.data shouldHaveSize 5
            page.data.map { it.domain.age } shouldBe listOf(0, 1, 2, 3, 4)
        }

        test("can get next subset of the data") {
            database.save((0..20).map { TestDomain("Jacob", it) })

            val page = database.selectAll(PageNumber(2), PageSize(5))

            page.data shouldHaveSize 5
            page.data.map { it.domain.age } shouldBe listOf(5, 6, 7, 8, 9)
        }

        test("can get all data if page size exceeds data") {
            database.save((0..20).map { TestDomain("Jacob", it) })

            val page = database.selectAll(PageNumber(1), PageSize(25))

            page.data shouldHaveSize 21
        }

        test("final page may not contain full page size") {
            database.save((0..20).map { TestDomain("Jacob", it) })

            val page = database.selectAll(PageNumber(5), PageSize(5))

            page.data shouldHaveSize 1
        }

        test("first page metadata is correct") {
            database.save((0..20).map { TestDomain("Jacob", it) })

            val page = database.selectAll(PageNumber(1), PageSize(5))

            page.pageSize.value shouldBe 5
            page.pageNumber.value shouldBe 1
            page.hasNextPage.value shouldBe true
            page.hasPreviousPage.value shouldBe false
            page.totalElements.value shouldBe 21
            page.totalPages.value shouldBe 5
        }

        test("middle page metadata is correct") {
            database.save((0..20).map { TestDomain("Jacob", it) })

            val page = database.selectAll(PageNumber(2), PageSize(5))

            page.pageSize.value shouldBe 5
            page.pageNumber.value shouldBe 2
            page.hasNextPage.value shouldBe true
            page.hasPreviousPage.value shouldBe true
            page.totalElements.value shouldBe 21
            page.totalPages.value shouldBe 5
        }

        test("final page metadata is correct") {
            database.save((0..20).map { TestDomain("Jacob", it) })

            val page = database.selectAll(PageNumber(5), PageSize(5))

            page.pageSize.value shouldBe 1
            page.pageNumber.value shouldBe 5
            page.hasNextPage.value shouldBe false
            page.hasPreviousPage.value shouldBe true
            page.totalElements.value shouldBe 21
            page.totalPages.value shouldBe 5
        }
    }
})
