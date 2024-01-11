package acceptance

import acceptance.setup.E2ETest
import config.transactionDatabase
import dao.Entity
import dao.Page
import domain.Transaction
import helpers.fixtures.aCreditTransaction
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.deserialize
import helpers.fixtures.withADateOf
import helpers.matchers.shouldContainDomain
import io.kotest.matchers.collections.shouldHaveSize
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus

class TransactionsTest : E2ETest({
    beforeEach { transactionDatabase.deleteAll() }

    test("can get a filtered subset of transactions back") {
        transactionDatabase.save(
            listOf(
                aDebitTransaction().withADateOf(2023, 1, 1),
                aDebitTransaction().withADateOf(2023, 6, 1),
                aDebitTransaction().withADateOf(2023, 12, 1),
                aCreditTransaction().withADateOf(2023, 1, 1),
                aCreditTransaction().withADateOf(2023, 6, 1),
                aCreditTransaction().withADateOf(2023, 12, 1)
            )
        )
        val response = client.get(
            "/transaction",
            queries = mapOf(
                "pageNumber" to "1",
                "pageSize" to "20",
                "start" to "2023-02-01",
                "end" to "2023-12-01",
                "type" to "credit"
            )
        )

        response shouldHaveStatus OK
        response.deserialize<Page<Entity<Transaction>>>().data.let { data ->
            data shouldHaveSize 1
            data shouldContainDomain aCreditTransaction().withADateOf(2023, 6, 1)
        }
    }

    test("passing no params returns all transactions for the page") {
        transactionDatabase.save(
            listOf(
                aDebitTransaction().withADateOf(2023, 1, 1),
                aDebitTransaction().withADateOf(2023, 6, 1),
                aDebitTransaction().withADateOf(2023, 12, 1),
                aCreditTransaction().withADateOf(2023, 1, 1),
                aCreditTransaction().withADateOf(2023, 6, 1),
                aCreditTransaction().withADateOf(2023, 12, 1)
            )
        )
        val response = client.get(
            "/transaction",
            queries = mapOf(
                "pageNumber" to "1",
                "pageSize" to "20"
            )
        )

        response shouldHaveStatus OK
        response.deserialize<Page<Entity<Transaction>>>().data shouldHaveSize 6
    }
})
