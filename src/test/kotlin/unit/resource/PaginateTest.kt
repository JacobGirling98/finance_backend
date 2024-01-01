package unit.resource

import domain.PageNumber
import domain.PageSize
import helpers.fixtures.Doubles
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import resource.paginate

class PaginateTest : FunSpec({

    val data = (0..20).map { Doubles.TestDomain("Jacob", it) }

    context("pagination") {
        test("can get a subset of the data") {
            val page = paginate(data, PageNumber(1), PageSize(5))

            page.data shouldHaveSize 5
            page.data.map { it.age } shouldBe listOf(0, 1, 2, 3, 4)
        }

        test("can get next subset of the data") {
            val page = paginate(data, PageNumber(2), PageSize(5))

            page.data shouldHaveSize 5
            page.data.map { it.age } shouldBe listOf(5, 6, 7, 8, 9)
        }

        test("can get all data if page size exceeds data") {
            val page = paginate(data, PageNumber(1), PageSize(25))

            page.data shouldHaveSize 21
        }

        test("final page may not contain full page size") {
            val page = paginate(data, PageNumber(5), PageSize(5))

            page.data shouldHaveSize 1
        }

        test("first page metadata is correct") {
            val page = paginate(data, PageNumber(1), PageSize(5))

            page.pageSize.value shouldBe 5
            page.pageNumber.value shouldBe 1
            page.hasNextPage.value shouldBe true
            page.hasPreviousPage.value shouldBe false
            page.totalElements.value shouldBe 21
            page.totalPages.value shouldBe 5
        }

        test("middle page metadata is correct") {
            val page = paginate(data, PageNumber(2), PageSize(5))

            page.pageSize.value shouldBe 5
            page.pageNumber.value shouldBe 2
            page.hasNextPage.value shouldBe true
            page.hasPreviousPage.value shouldBe true
            page.totalElements.value shouldBe 21
            page.totalPages.value shouldBe 5
        }

        test("final page metadata is correct") {
            val page = paginate(data, PageNumber(5), PageSize(5))

            page.pageSize.value shouldBe 1
            page.pageNumber.value shouldBe 5
            page.hasNextPage.value shouldBe false
            page.hasPreviousPage.value shouldBe true
            page.totalElements.value shouldBe 21
            page.totalPages.value shouldBe 5
        }
    }
})