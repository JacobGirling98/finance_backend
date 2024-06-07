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
import helpers.fixtures.aBankTransferTransaction
import helpers.fixtures.aCreditTransaction
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.aPersonalTransferTransaction
import helpers.fixtures.anIncomeTransaction
import helpers.fixtures.deserialize
import helpers.fixtures.withADateOf
import helpers.matchers.shouldContainDomain
import http.model.Transaction.TransactionConfirmation
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NO_CONTENT
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

    test("can add multiple debit transactions") {
        val request = """
            [
                {
                    "date": "2020-01-01",
                    "category": "Food",
                    "value": 1.5,
                    "description": "Banana",
                    "quantity": 1
                },
                {
                    "date": "2021-01-01",
                    "category": "Food",
                    "value": 3.0,
                    "description": "Banana",
                    "quantity": 1
                }
            ]
        """.trimIndent()

        val response = client.post("/transaction/multiple/debit", request)

        response shouldHaveStatus CREATED

        val responseConfirmation = response.deserialize<TransactionConfirmation>()

        responseConfirmation.transactionCount shouldBe 2
        responseConfirmation.value shouldBe 4.5
        responseConfirmation.ids shouldHaveSize 2

        val expectedTransactions = listOf(
            Transaction(
                date = Date.of(2021, 1, 1),
                category = Category("Food"),
                value = Value.of(3.0),
                description = Description("Banana"),
                type = TransactionType.DEBIT,
                outgoing = Outgoing(true),
                quantity = Quantity(1),
                recipient = null,
                inbound = null,
                outbound = null,
                source = null,
                addedBy = AddedBy("finance-app")
            ),
            Transaction(
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
        )

        responseConfirmation.ids.map { matchingEntity(it).domain } shouldContainExactlyInAnyOrder expectedTransactions
    }

    test("can add multiple credit transactions") {
        val request = """
            [
                {
                    "date": "2020-01-01",
                    "category": "Food",
                    "value": 1.5,
                    "description": "Banana",
                    "quantity": 1
                },
                {
                    "date": "2021-01-01",
                    "category": "Food",
                    "value": 3.0,
                    "description": "Banana",
                    "quantity": 1
                }
            ]
        """.trimIndent()

        val response = client.post("/transaction/multiple/credit", request)

        response shouldHaveStatus CREATED

        val responseConfirmation = response.deserialize<TransactionConfirmation>()

        responseConfirmation.transactionCount shouldBe 2
        responseConfirmation.value shouldBe 4.5
        responseConfirmation.ids shouldHaveSize 2

        val expectedTransactions = listOf(
            Transaction(
                date = Date.of(2021, 1, 1),
                category = Category("Food"),
                value = Value.of(3.0),
                description = Description("Banana"),
                type = TransactionType.CREDIT,
                outgoing = Outgoing(true),
                quantity = Quantity(1),
                recipient = null,
                inbound = null,
                outbound = null,
                source = null,
                addedBy = AddedBy("finance-app")
            ),
            Transaction(
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
        )

        responseConfirmation.ids.map { matchingEntity(it).domain } shouldContainExactlyInAnyOrder expectedTransactions
    }

    test("can add multiple bank transfer transactions") {
        val request = """
            [
                {
                    "date": "2020-01-01",
                    "category": "Food",
                    "value": 1.5,
                    "description": "Banana",
                    "quantity": 1,
                    "recipient": "Jacob"
                },
                {
                    "date": "2021-01-01",
                    "category": "Food",
                    "value": 3.0,
                    "description": "Banana",
                    "quantity": 1,
                    "recipient": "Jacob"
                }
            ]
        """.trimIndent()

        val response = client.post("/transaction/multiple/bank-transfer", request)

        response shouldHaveStatus CREATED

        val responseConfirmation = response.deserialize<TransactionConfirmation>()

        responseConfirmation.transactionCount shouldBe 2
        responseConfirmation.value shouldBe 4.5
        responseConfirmation.ids shouldHaveSize 2

        val expectedTransactions = listOf(
            Transaction(
                date = Date.of(2021, 1, 1),
                category = Category("Food"),
                value = Value.of(3.0),
                description = Description("Banana"),
                type = TransactionType.BANK_TRANSFER,
                outgoing = Outgoing(true),
                quantity = Quantity(1),
                recipient = Recipient("Jacob"),
                inbound = null,
                outbound = null,
                source = null,
                addedBy = AddedBy("finance-app")
            ),
            Transaction(
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
        )

        responseConfirmation.ids.map { matchingEntity(it).domain } shouldContainExactlyInAnyOrder expectedTransactions
    }

    test("can add multiple personal transfer transactions") {
        val request = """
            [
                {
                    "date": "2020-01-01",
                    "category": "Food",
                    "value": 1.5,
                    "description": "Banana",
                    "outbound": "Current",
                    "inbound": "Savings"
                },
                {
                    "date": "2021-01-01",
                    "category": "Food",
                    "value": 3.0,
                    "description": "Banana",
                    "outbound": "Current",
                    "inbound": "Savings"
                }
            ]
        """.trimIndent()

        val response = client.post("/transaction/multiple/personal-transfer", request)

        response shouldHaveStatus CREATED

        val responseConfirmation = response.deserialize<TransactionConfirmation>()

        responseConfirmation.transactionCount shouldBe 2
        responseConfirmation.value shouldBe 4.5
        responseConfirmation.ids shouldHaveSize 2

        val expectedTransactions = listOf(
            Transaction(
                date = Date.of(2021, 1, 1),
                category = Category("Food"),
                value = Value.of(3.0),
                description = Description("Banana"),
                type = TransactionType.PERSONAL_TRANSFER,
                outgoing = Outgoing(false),
                quantity = Quantity(1),
                recipient = null,
                inbound = Inbound("Savings"),
                outbound = Outbound("Current"),
                source = null,
                addedBy = AddedBy("finance-app")
            ),
            Transaction(
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
        )

        responseConfirmation.ids.map { matchingEntity(it).domain } shouldContainExactlyInAnyOrder expectedTransactions
    }

    test("can add multiple income transactions") {
        val request = """
            [
                {
                    "date": "2020-01-01",
                    "category": "Food",
                    "value": 1.5,
                    "description": "Banana",
                    "source": "Work"
                },
                {
                    "date": "2021-01-01",
                    "category": "Food",
                    "value": 3.0,
                    "description": "Banana",
                    "source": "Work"
                }
            ]
        """.trimIndent()

        val response = client.post("/transaction/multiple/income", request)

        response shouldHaveStatus CREATED

        val responseConfirmation = response.deserialize<TransactionConfirmation>()

        responseConfirmation.transactionCount shouldBe 2
        responseConfirmation.value shouldBe 4.5
        responseConfirmation.ids shouldHaveSize 2

        val expectedTransactions = listOf(
            Transaction(
                date = Date.of(2021, 1, 1),
                category = Category("Food"),
                value = Value.of(3.0),
                description = Description("Banana"),
                type = TransactionType.INCOME,
                outgoing = Outgoing(false),
                quantity = Quantity(1),
                recipient = null,
                inbound = null,
                outbound = null,
                source = Source("Work"),
                addedBy = AddedBy("finance-app")
            ),
            Transaction(
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
        )

        responseConfirmation.ids.map { matchingEntity(it).domain } shouldContainExactlyInAnyOrder expectedTransactions
    }

    test("can update a debit transaction") {
        val oldTransaction = aDebitTransaction()
        val id = transactionDatabase.save(oldTransaction)

        val request = """
            {
                "date": "2024-01-01",
                "category": "Gaming",
                "value": 50.0,
                "description": "PC",
                "quantity": 1
            }
        """.trimIndent()

        val response = client.put("/transaction/debit/$id", request)

        response shouldHaveStatus NO_CONTENT
        matchingEntity(id).domain shouldBe Transaction(
            date = Date.of(2024, 1, 1),
            category = Category("Gaming"),
            value = Value.of(50.0),
            description = Description("PC"),
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

    test("can update a credit transaction") {
        val oldTransaction = aCreditTransaction()
        val id = transactionDatabase.save(oldTransaction)

        val request = """
            {
                "date": "2024-01-01",
                "category": "Gaming",
                "value": 50.0,
                "description": "PC",
                "quantity": 1
            }
        """.trimIndent()

        val response = client.put("/transaction/credit/$id", request)

        response shouldHaveStatus NO_CONTENT
        matchingEntity(id).domain shouldBe Transaction(
            date = Date.of(2024, 1, 1),
            category = Category("Gaming"),
            value = Value.of(50.0),
            description = Description("PC"),
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

    test("can update a bank transfer transaction") {
        val oldTransaction = aBankTransferTransaction()
        val id = transactionDatabase.save(oldTransaction)

        val request = """
            {
                "date": "2024-01-01",
                "category": "Gaming",
                "value": 50.0,
                "description": "PC",
                "quantity": 1,
                "recipient": "Friend"
            }
        """.trimIndent()

        val response = client.put("/transaction/bank-transfer/$id", request)

        response shouldHaveStatus NO_CONTENT
        matchingEntity(id).domain shouldBe Transaction(
            date = Date.of(2024, 1, 1),
            category = Category("Gaming"),
            value = Value.of(50.0),
            description = Description("PC"),
            type = TransactionType.BANK_TRANSFER,
            outgoing = Outgoing(true),
            quantity = Quantity(1),
            recipient = Recipient("Friend"),
            inbound = null,
            outbound = null,
            source = null,
            addedBy = AddedBy("finance-app")
        )
    }

    test("can update a personal transfer transaction") {
        val oldTransaction = aPersonalTransferTransaction()
        val id = transactionDatabase.save(oldTransaction)

        val request = """
            {
                "date": "2024-01-01",
                "category": "Gaming",
                "value": 50.0,
                "description": "PC",
                "outbound": "Current",
                "inbound": "Savings"
            }
        """.trimIndent()

        val response = client.put("/transaction/personal-transfer/$id", request)

        response shouldHaveStatus NO_CONTENT
        matchingEntity(id).domain shouldBe Transaction(
            date = Date.of(2024, 1, 1),
            category = Category("Gaming"),
            value = Value.of(50.0),
            description = Description("PC"),
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

    test("can update an income transaction") {
        val oldTransaction = anIncomeTransaction()
        val id = transactionDatabase.save(oldTransaction)

        val request = """
            {
                "date": "2024-01-01",
                "category": "Gaming",
                "value": 50.0,
                "description": "PC",
                "source": "Work"
            }
        """.trimIndent()

        val response = client.put("/transaction/income/$id", request)

        response shouldHaveStatus NO_CONTENT
        matchingEntity(id).domain shouldBe Transaction(
            date = Date.of(2024, 1, 1),
            category = Category("Gaming"),
            value = Value.of(50.0),
            description = Description("PC"),
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

private fun matchingEntity(id: UUID) = transactionDatabase.selectAll().first { it.id == id }
