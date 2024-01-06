package unit.resource

import dao.Database
import dao.entityOf
import domain.PageNumber
import domain.PageSize
import domain.Transaction
import domain.TransactionType.CREDIT
import helpers.fixtures.*
import helpers.matchers.shouldContainDomain
import helpers.matchers.shouldNotContainDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import resource.TransactionProcessor
import java.time.LocalDate
import java.util.*

class TransactionProcessorTest : FunSpec({
    val database = mockk<Database<Transaction, UUID>>()
    val processor = TransactionProcessor(database)

    test("can get most recent transaction by me") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2022, 3, 1).addedBy("finance-app")),
            entityOf(aDebitTransaction().withADateOf(2022, 2, 1).addedBy("Jacob")),
            entityOf(aDebitTransaction().withADateOf(2022, 1, 1).addedBy("Jacob")),
        )
        every { database.selectAll() } returns transactions

        processor.mostRecentUserTransaction()?.value shouldBe LocalDate.of(2022, 2, 1)
    }

    test("can filter transactions") {
        val transactions = listOf(
            entityOf(aDebitTransaction()),
            entityOf(aCreditTransaction()),
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
            entityOf(aCreditTransaction().withADescriptionOf("Something else")),
        )
        every { database.selectAll() } returns transactions

        val filtered = processor.search("test", PageNumber(1), PageSize(3)) { it.domain.type == CREDIT }

        filtered.data shouldContainDomain aCreditTransaction().withADescriptionOf("Testing")
        filtered.data shouldNotContainDomain aDebitTransaction().withADescriptionOf("Testing")
        filtered.data shouldNotContainDomain aCreditTransaction().withADescriptionOf("Something else")
    }
})

