package unit.http.handler

import dao.Database
import domain.*
import domain.Date
import domain.TransactionType.DEBIT
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.aPage
import helpers.fixtures.deserialize
import http.handler.*
import http.model.Transaction.TransactionConfirmation
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class TransactionHandlerTest : FunSpec({
    val database = mockk<Database<Transaction, UUID>>(relaxed = true)

    every { database.save(any<Transaction>()) } returns UUID.randomUUID()
    every { database.save(any<List<Transaction>>()) } returns listOf(UUID.randomUUID())

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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus NO_CONTENT
        verify {
            database.save(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    TransactionType.CREDIT,
                    Outgoing(true),
                    Quantity(2),
                    addedBy = AddedBy("Jacob")
                )
            )
        }
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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus NO_CONTENT
        verify {
            database.save(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    TransactionType.BANK_TRANSFER,
                    Outgoing(true),
                    Quantity(1),
                    Recipient("Friend"),
                    addedBy = AddedBy("Jacob")
                )
            )
        }
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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus NO_CONTENT
        verify {
            database.save(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    TransactionType.PERSONAL_TRANSFER,
                    Outgoing(false),
                    Quantity(1),
                    outbound = Outbound("Current"),
                    inbound = Inbound("Savings"),
                    addedBy = AddedBy("Jacob")
                )
            )
        }
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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus NO_CONTENT
        verify {
            database.save(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    TransactionType.INCOME,
                    Outgoing(false),
                    Quantity(1),
                    source = Source("Work"),
                    addedBy = AddedBy("Jacob")
                )
            )
        }
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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus OK
        verify {
            database.save(
                listOf(
                    Transaction(
                        Date(LocalDate.of(2020, 10, 12)),
                        Category("Food"),
                        Value(BigDecimal("12.50")),
                        Description("Cake"),
                        TransactionType.CREDIT,
                        Outgoing(true),
                        Quantity(2),
                        addedBy = AddedBy("Jacob")
                    ),
                    Transaction(
                        Date(LocalDate.of(2020, 10, 15)),
                        Category("Tech"),
                        Value(BigDecimal("500.00")),
                        Description("Speaker"),
                        TransactionType.CREDIT,
                        Outgoing(true),
                        Quantity(1),
                        addedBy = AddedBy("Jacob")
                    )
                )
            )
        }
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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus OK
        verify {
            database.save(
                listOf(
                    Transaction(
                        Date(LocalDate.of(2020, 10, 12)),
                        Category("Food"),
                        Value(BigDecimal("12.50")),
                        Description("Cake"),
                        TransactionType.BANK_TRANSFER,
                        Outgoing(true),
                        Quantity(1),
                        Recipient("Friend"),
                        addedBy = AddedBy("Jacob")
                    ),
                    Transaction(
                        Date(LocalDate.of(2020, 10, 15)),
                        Category("Tech"),
                        Value(BigDecimal("500.00")),
                        Description("Speaker"),
                        TransactionType.BANK_TRANSFER,
                        Outgoing(true),
                        Quantity(1),
                        Recipient("Family"),
                        addedBy = AddedBy("Jacob")
                    )
                )
            )
        }
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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus OK

        verify {
            database.save(
                listOf(
                    Transaction(
                        Date(LocalDate.of(2020, 10, 12)),
                        Category("Food"),
                        Value(BigDecimal("12.50")),
                        Description("Cake"),
                        TransactionType.PERSONAL_TRANSFER,
                        Outgoing(false),
                        Quantity(1),
                        outbound = Outbound("Current"),
                        inbound = Inbound("Savings"),
                        addedBy = AddedBy("Jacob")
                    ),
                    Transaction(
                        Date(LocalDate.of(2020, 10, 15)),
                        Category("Tech"),
                        Value(BigDecimal("500.00")),
                        Description("Speaker"),
                        TransactionType.PERSONAL_TRANSFER,
                        Outgoing(false),
                        Quantity(1),
                        outbound = Outbound("Current"),
                        inbound = Inbound("Credit"),
                        addedBy = AddedBy("Jacob")
                    )
                )
            )
        }
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
            ).header("user", "Jacob")
        )

        response shouldHaveStatus OK
        verify {
            database.save(
                listOf(
                    Transaction(
                        Date(LocalDate.of(2020, 10, 12)),
                        Category("Food"),
                        Value(BigDecimal("12.50")),
                        Description("Cake"),
                        TransactionType.INCOME,
                        Outgoing(false),
                        Quantity(1),
                        source = Source("Work"),
                        addedBy = AddedBy("Jacob")
                    ),
                    Transaction(
                        Date(LocalDate.of(2020, 10, 15)),
                        Category("Wages"),
                        Value(BigDecimal("500.00")),
                        Description("Wages"),
                        TransactionType.INCOME,
                        Outgoing(false),
                        Quantity(1),
                        source = Source("Work"),
                        addedBy = AddedBy("Jacob")
                    )
                )
            )
        }
    }

    test("posting debit/credit transactions returns number saved and total value") {
        val handler =
            postCreditDebitListHandler(DEBIT) { transactions -> List(transactions.size) { UUID.randomUUID() } }

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
            ).header("user", "Jacob")
        )

        response.deserialize<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }

    test("posting bank transfer transactions returns number saved and total value") {
        val handler = postBankTransferListHandler { transactions -> List(transactions.size) { UUID.randomUUID() } }

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
            ).header("user", "Jacob")
        )

        response.deserialize<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }

    test("posting personal transfer transactions returns number saved and total value") {
        val handler = postPersonalTransferListHandler { transactions -> List(transactions.size) { UUID.randomUUID() } }

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
            ).header("user", "Jacob")
        )

        response.deserialize<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }

    test("posting income transactions returns number saved and total value") {
        val handler = postIncomeListHandler { transactions -> List(transactions.size) { UUID.randomUUID() } }

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
            ).header("user", "Jacob")
        )

        response.deserialize<TransactionConfirmation>() shouldBe TransactionConfirmation(
            transactionCount = 2,
            value = 512.50f
        )
    }

    test("added by defaults if no header given") {
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
        verify {
            database.save(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    TransactionType.CREDIT,
                    Outgoing(true),
                    Quantity(2),
                    addedBy = AddedBy("finance-app")
                )
            )
        }
    }

    context("pagination") {
        test("can extract page number and page size from the request") {
            var pageNumber = 0
            var pageSize = 0
            val handler = paginatedTransactionsHandler { number, size ->
                pageNumber = number.value
                pageSize = size.value
                aPage(aDebitTransaction())
            }

            handler(Request(Method.GET, "/").query("pageSize", "5").query("pageNumber", "1"))

            pageNumber shouldBe 1
            pageSize shouldBe 5
        }
    }
})
