package acceptance

import acceptance.setup.E2ETest
import config.transactionDatabase
import dao.Entity
import dao.Page
import domain.AddedBy
import domain.Category
import domain.Date
import domain.Description
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.Transaction
import domain.TransactionType
import domain.Value
import helpers.fixtures.aCreditTransaction
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.deserialize
import helpers.fixtures.withADateOf
import helpers.matchers.shouldContainDomain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import java.util.*

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

    test("can add a debit transaction") {
        val request = """
            {
                "date": "2020-01-01",
                "category": "Food",
                "value": 1.5,
                "description": "Banana",
                "quantity": 1
            }
        """.trimIndent()

        val response = client.post("/transaction/debit", request)

        response shouldHaveStatus CREATED
        response.bodyString().shouldBeUUID()

        val expectedEntity = matchingEntity(response)

        expectedEntity.domain shouldBe Transaction(
            date = Date.of(2020, 1, 1),
            category = Category("Food"),
            value = Value.of(1.5),
            description = Description("Banana"),
            type = TransactionType.DEBIT,
            outgoing = Outgoing(true),
            quantity = Quantity(1),
            recipient = null,
            inbound = null,
            outbound = null,
            source = null,
            addedBy = AddedBy("finance-app")
        )
    }

    test("can add a credit transaction") {
        val request = """
            {
                "date": "2020-01-01",
                "category": "Food",
                "value": 1.5,
                "description": "Banana",
                "quantity": 1
            }
        """.trimIndent()

        val response = client.post("/transaction/credit", request)

        response shouldHaveStatus CREATED
        response.bodyString().shouldBeUUID()

        matchingEntity(response).domain shouldBe Transaction(
            date = Date.of(2020, 1, 1),
            category = Category("Food"),
            value = Value.of(1.5),
            description = Description("Banana"),
            type = TransactionType.CREDIT,
            outgoing = Outgoing(true),
            quantity = Quantity(1),
            recipient = null,
            inbound = null,
            outbound = null,
            source = null,
            addedBy = AddedBy("finance-app")
        )
    }

    test("can add a bank transfer transaction") {
        val request = """
            {
                "date": "2020-01-01",
                "category": "Food",
                "value": 1.5,
                "description": "Banana",
                "quantity": 1,
                "recipient": "Jacob"
            }
        """.trimIndent()

        val response = client.post("/transaction/bank-transfer", request)

        response shouldHaveStatus CREATED
        response.bodyString().shouldBeUUID()

        matchingEntity(response).domain shouldBe Transaction(
            date = Date.of(2020, 1, 1),
            category = Category("Food"),
            value = Value.of(1.5),
            description = Description("Banana"),
            type = TransactionType.BANK_TRANSFER,
            outgoing = Outgoing(true),
            quantity = Quantity(1),
            recipient = Recipient("Jacob"),
            inbound = null,
            outbound = null,
            source = null,
            addedBy = AddedBy("finance-app")
        )
    }

    test("can add a personal transfer transaction") {
        val request = """
            {
                "date": "2020-01-01",
                "category": "Food",
                "value": 1.5,
                "description": "Banana",
                "outbound": "Current",
                "inbound": "Savings"
            }
        """.trimIndent()

        val response = client.post("/transaction/personal-transfer", request)

        response shouldHaveStatus CREATED
        response.bodyString().shouldBeUUID()

        matchingEntity(response).domain shouldBe Transaction(
            date = Date.of(2020, 1, 1),
            category = Category("Food"),
            value = Value.of(1.5),
            description = Description("Banana"),
            type = TransactionType.PERSONAL_TRANSFER,
            outgoing = Outgoing(false),
            quantity = Quantity(1),
            recipient = null,
            inbound = Inbound("Savings"),
            outbound = Outbound("Current"),
            source = null,
            addedBy = AddedBy("finance-app")
        )
    }

    test("can add an income transaction") {
        val request = """
            {
                "date": "2020-01-01",
                "category": "Food",
                "value": 1.5,
                "description": "Banana",
                "source": "Work"
            }
        """.trimIndent()

        val response = client.post("/transaction/income", request)

        response shouldHaveStatus CREATED

        matchingEntity(response).domain shouldBe Transaction(
            date = Date.of(2020, 1, 1),
            category = Category("Food"),
            value = Value.of(1.5),
            description = Description("Banana"),
            type = TransactionType.INCOME,
            outgoing = Outgoing(false),
            quantity = Quantity(1),
            recipient = null,
            inbound = null,
            outbound = null,
            source = Source("Work"),
            addedBy = AddedBy("finance-app")
        )
    }
})

private fun matchingEntity(response: Response) =
    transactionDatabase.selectAll().first { it.id == UUID.fromString(response.bodyString()) }
