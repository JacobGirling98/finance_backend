package unit.http.handler

import dao.Database
import domain.*
import domain.TransactionType.DEBIT
import http.handler.*
import unit.fixtures.toObject
import http.model.TransactionConfirmation
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import java.math.BigDecimal
import java.time.LocalDate

class TransactionHandlerTest : FunSpec({
    val database = TestDatabase()

    test("can post a credit-debit transaction") {
        val handler = postCreditDebitHandler(TransactionType.CREDIT) { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                {
                    "date": "2020-10-12",
                    "category": "Food",
                    "value": 12.50,
                    "description": "Cake",
                    "quantity": 2
                }
            """.trimIndent()
            )
        )

        response shouldHaveStatus NO_CONTENT
        database.arguments shouldHaveSingleElement Transaction(
            Date(LocalDate.of(2020, 10, 12)),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Cake"),
            TransactionType.CREDIT,
            Outgoing(true),
            Quantity(2)
        )
    }

    test("can post a bank transfer transaction") {
        val handler = postBankTransferHandler { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                {
                    "date": "2020-10-12",
                    "category": "Food",
                    "value": 12.50,
                    "description": "Cake",
                    "quantity": 1,
                    "recipient": "Friend"
                }
            """.trimIndent()
            )
        )

        response shouldHaveStatus NO_CONTENT
        database.arguments shouldHaveSingleElement Transaction(
            Date(LocalDate.of(2020, 10, 12)),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Cake"),
            TransactionType.BANK_TRANSFER,
            Outgoing(true),
            Quantity(1),
            Recipient("Friend")
        )
    }

    test("can post a personal transfer transaction") {
        val handler = postPersonalTransferHandler { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                {
                    "date": "2020-10-12",
                    "category": "Food",
                    "value": 12.50,
                    "description": "Cake",
                    "outbound": "Current",
                    "inbound": "Savings"
                }
            """.trimIndent()
            )
        )

        response shouldHaveStatus NO_CONTENT
        database.arguments shouldHaveSingleElement Transaction(
            Date(LocalDate.of(2020, 10, 12)),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Cake"),
            TransactionType.PERSONAL_TRANSFER,
            Outgoing(false),
            Quantity(1),
            outbound = Outbound("Current"),
            inbound = Inbound("Savings")
        )
    }

    test("can post an income transaction") {
        val handler = postIncomeHandler { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                {
                    "date": "2020-10-12",
                    "category": "Food",
                    "value": 12.50,
                    "description": "Cake",
                    "source": "Work"
                }
            """.trimIndent()
            )
        )

        response shouldHaveStatus NO_CONTENT
        database.arguments shouldHaveSingleElement Transaction(
            Date(LocalDate.of(2020, 10, 12)),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Cake"),
            TransactionType.INCOME,
            Outgoing(false),
            Quantity(1),
            source = Source("Work")
        )
    }

    test("can post multiple credit-debit transactions") {
        val handler = postCreditDebitListHandler(TransactionType.CREDIT) { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "quantity": 2
                    },
                    {
                        "date": "2020-10-15",
                        "category": "Tech",
                        "value": 500.00,
                        "description": "Speaker",
                        "quantity": 1
                    }
                ]
            """.trimIndent()
            )
        )

        response shouldHaveStatus OK
        database.arguments
            .shouldContain(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    TransactionType.CREDIT,
                    Outgoing(true),
                    Quantity(2)
                )
            )
            .shouldContain(
                Transaction(
                    Date(LocalDate.of(2020, 10, 15)),
                    Category("Tech"),
                    Value(BigDecimal("500.00")),
                    Description("Speaker"),
                    TransactionType.CREDIT,
                    Outgoing(true),
                    Quantity(1)
                )
            )
    }

    test("can post multiple bank transfer transactions") {
        val handler = postBankTransferListHandler { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "quantity": 1,
                        "recipient": "Friend"
                    },
                    {
                        "date": "2020-10-15",
                        "category": "Tech",
                        "value": 500.00,
                        "description": "Speaker",
                        "quantity": 1,
                        "recipient": "Family"
                    }
                ]
            """.trimIndent()
            )
        )

        response shouldHaveStatus OK
        database.arguments.shouldContain(
            Transaction(
                Date(LocalDate.of(2020, 10, 12)),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Cake"),
                TransactionType.BANK_TRANSFER,
                Outgoing(true),
                Quantity(1),
                Recipient("Friend")
            )
        ).shouldContain(
            Transaction(
                Date(LocalDate.of(2020, 10, 15)),
                Category("Tech"),
                Value(BigDecimal("500.00")),
                Description("Speaker"),
                TransactionType.BANK_TRANSFER,
                Outgoing(true),
                Quantity(1),
                Recipient("Family")
            )
        )
    }

    test("can post multiple personal transfer transactions") {
        val handler = postPersonalTransferListHandler { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "outbound": "Current",
                        "inbound": "Savings"
                    },
                    {   
                        "date": "2020-10-15",
                        "category": "Tech",
                        "value": 500.00,
                        "description": "Speaker",
                        "outbound": "Current",
                        "inbound": "Credit"
                    }
                ]
            """.trimIndent()
            )
        )

        response shouldHaveStatus OK

        database.arguments
            .shouldContain(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    TransactionType.PERSONAL_TRANSFER,
                    Outgoing(false),
                    Quantity(1),
                    outbound = Outbound("Current"),
                    inbound = Inbound("Savings")
                )

            ).shouldContain(
                Transaction(
                    Date(LocalDate.of(2020, 10, 15)),
                    Category("Tech"),
                    Value(BigDecimal("500.00")),
                    Description("Speaker"),
                    TransactionType.PERSONAL_TRANSFER,
                    Outgoing(false),
                    Quantity(1),
                    outbound = Outbound("Current"),
                    inbound = Inbound("Credit")
                )

            )
    }

    test("can post multiple income transactions") {
        val handler = postIncomeListHandler { database.save(it) }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "source": "Work"
                    },
                    {
                        "date": "2020-10-15",
                        "category": "Wages",
                        "value": 500.00,
                        "description": "Wages",
                        "source": "Work"
                    }
                ]
            """.trimIndent()
            )
        )

        response shouldHaveStatus OK
        database.arguments.shouldContain(
            Transaction(
                Date(LocalDate.of(2020, 10, 12)),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Cake"),
                TransactionType.INCOME,
                Outgoing(false),
                Quantity(1),
                source = Source("Work")
            )
        ).shouldContain(
            Transaction(
                Date(LocalDate.of(2020, 10, 15)),
                Category("Wages"),
                Value(BigDecimal("500.00")),
                Description("Wages"),
                TransactionType.INCOME,
                Outgoing(false),
                Quantity(1),
                source = Source("Work")
            )
        )
    }

    test("posting debit/credit transactions returns number saved and total value") {
        val handler = postCreditDebitListHandler(DEBIT) { transactions -> transactions.size }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "quantity": 2
                    },
                    {
                        "date": "2020-10-15",
                        "category": "Tech",
                        "value": 500.00,
                        "description": "Speaker",
                        "quantity": 1
                    }
                ]
            """.trimIndent()
            )
        )

        response.body.toObject<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }

    test("posting bank transfer transactions returns number saved and total value") {
        val handler = postBankTransferListHandler { transactions -> transactions.size }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "quantity": 1,
                        "recipient": "Friend"
                    },
                    {
                        "date": "2020-10-15",
                        "category": "Tech",
                        "value": 500.00,
                        "description": "Speaker",
                        "quantity": 1,
                        "recipient": "Family"
                    }
                ]
            """.trimIndent()
            )
        )

        response.body.toObject<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }

    test("posting personal transfer transactions returns number saved and total value") {
        val handler = postPersonalTransferListHandler { transactions -> transactions.size }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "outbound": "Current",
                        "inbound": "Savings"
                    },
                    {   
                        "date": "2020-10-15",
                        "category": "Tech",
                        "value": 500.00,
                        "description": "Speaker",
                        "outbound": "Current",
                        "inbound": "Credit"
                    }
                ]
            """.trimIndent()
            )
        )

        response.body.toObject<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }

    test("posting income transactions returns number saved and total value") {
        val handler = postIncomeListHandler { transactions -> transactions.size }

        val response = handler(
            Request(Method.POST, "/").body(
                """
                [
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "source": "Work"
                    },
                    {
                        "date": "2020-10-15",
                        "category": "Wages",
                        "value": 500.00,
                        "description": "Wages",
                        "source": "Work"
                    }
                ]
            """.trimIndent()
            )
        )

        response.body.toObject<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }
})

private class TestDatabase : Database<Transaction> {

    var arguments = mutableListOf<Transaction>()

    override fun save(data: Transaction) {
        arguments.add(data)
    }

    override fun save(data: List<Transaction>): Int {
        data.forEach { arguments.add(it) }
        return data.size
    }

}