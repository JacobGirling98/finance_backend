package unit.resource

import dao.UUIDDatabase
import dao.entityOf
import domain.Category
import domain.DateRange
import domain.EndDate
import domain.PageNumber
import domain.PageSize
import domain.StartDate
import domain.Transaction
import domain.TransactionType.CREDIT
import helpers.fixtures.aCreditTransaction
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.addedBy
import helpers.fixtures.withACategoryOf
import helpers.fixtures.withADateOf
import helpers.fixtures.withADescriptionOf
import helpers.matchers.shouldContainDomain
import helpers.matchers.shouldNotContainDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import resource.TransactionProcessor
import java.time.LocalDate

class TransactionProcessorTest : FunSpec({
    val database = mockk<UUIDDatabase<Transaction>>()
    val processor = TransactionProcessor(database)

    test("can get most recent transaction by me") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2022, 3, 1).addedBy("finance-app")),
            entityOf(aDebitTransaction().withADateOf(2022, 2, 1).addedBy("Jacob")),
            entityOf(aDebitTransaction().withADateOf(2022, 1, 1).addedBy("Jacob"))
        )
        every { database.selectAll() } returns transactions

        processor.mostRecentUserTransaction()?.value shouldBe LocalDate.of(2022, 2, 1)
    }

    test("can filter transactions") {
        val transactions = listOf(
            entityOf(aDebitTransaction()),
            entityOf(aCreditTransaction())
        )
        every { database.selectAll() } returns transactions

        val filtered = processor.selectBy(PageNumber(1), PageSize(2)) { it.domain.type == CREDIT }

        filtered.data shouldContainDomain aCreditTransaction()
        filtered.data shouldNotContainDomain aDebitTransaction()
    }

    test("can search and filter transactions") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADescriptionOf("Testing")),
            entityOf(aCreditTransaction().withADescriptionOf("Testing")),
            entityOf(aCreditTransaction().withADescriptionOf("Something else"))
        )
        every { database.selectAll() } returns transactions

        val filtered = processor.search("test", PageNumber(1), PageSize(3)) { it.domain.type == CREDIT }

        filtered.data shouldContainDomain aCreditTransaction().withADescriptionOf("Testing")
        filtered.data shouldNotContainDomain aDebitTransaction().withADescriptionOf("Testing")
        filtered.data shouldNotContainDomain aCreditTransaction().withADescriptionOf("Something else")
    }

    test("can get transactions by categories and date") {
        val wantedTransactions = listOf(entityOf(aDebitTransaction().withACategoryOf("Food").withADateOf(2024, 1, 1)))
        val unwantedTransactions = listOf(
            entityOf(aDebitTransaction().withACategoryOf("Food").withADateOf(2024, 2, 1)),
            entityOf(aDebitTransaction().withACategoryOf("Tech").withADateOf(2024, 1, 1))
        )
        every { database.selectAll() } returns wantedTransactions + unwantedTransactions

        val transactions = processor.transactionsBy(
            Category("Food"),
            DateRange(StartDate.of(2024, 1, 1), EndDate.of(2024, 2, 1))
        )

        transactions shouldBe wantedTransactions
    }
})
