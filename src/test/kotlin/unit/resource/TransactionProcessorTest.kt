package unit.resource

import dao.Database
import dao.entityOf
import domain.Transaction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import resource.TransactionProcessor
import unit.fixtures.aDebitTransaction
import unit.fixtures.addedBy
import unit.fixtures.withADateOf
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
})

