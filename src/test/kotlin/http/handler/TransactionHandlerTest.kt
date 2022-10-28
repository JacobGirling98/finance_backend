package http.handler

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.hasElement
import dao.Database
import domain.*
import domain.TransactionType.*
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

private class TestDatabase : Database<Transaction> {

    var arguments = mutableListOf<Transaction>()

    override fun save(data: Transaction) {
        arguments.add(data)
    }

    override fun save(data: List<Transaction>) {
        data.forEach { arguments.add(it) }
    }

}

class TransactionHandlerTest {

    private val database = TestDatabase()

    @Test
    fun `can post a credit-debit transaction`() {
        val handler = postCreditDebitHandler(CREDIT) { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    CREDIT,
                    Outgoing(true),
                    Quantity(2)
                )
            )
        )
    }

    @Test
    fun `can post a bank transfer transaction`() {
        val handler = postBankTransferHandler { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    BANK_TRANSFER,
                    Outgoing(true),
                    Quantity(1),
                    Recipient("Friend")
                )
            )
        )
    }

    @Test
    fun `can post a personal transfer transaction`() {
        val handler = postPersonalTransferHandler { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    PERSONAL_TRANSFER,
                    Outgoing(false),
                    Quantity(1),
                    outbound = Outbound("Current"),
                    inbound = Inbound("Savings")
                )
            )
        )
    }

    @Test
    fun `can post an income transaction`() {
        val handler = postIncomeHandler { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    INCOME,
                    Outgoing(false),
                    Quantity(1),
                    source = Source("Work")
                )
            )
        )
    }

    @Test
    fun `can post multiple credit-debit transactions`() {
        val handler = postCreditDebitListHandler(CREDIT) { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    CREDIT,
                    Outgoing(true),
                    Quantity(2)
                )
            )
        )
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 15)),
                    Category("Tech"),
                    Value(BigDecimal("500.00")),
                    Description("Speaker"),
                    CREDIT,
                    Outgoing(true),
                    Quantity(1)
                )
            )
        )
    }

    @Test
    fun `can post multiple bank transfer transactions`() {
        val handler = postBankTransferListHandler { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    BANK_TRANSFER,
                    Outgoing(true),
                    Quantity(1),
                    Recipient("Friend")
                )
            )
        )
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 15)),
                    Category("Tech"),
                    Value(BigDecimal("500.00")),
                    Description("Speaker"),
                    BANK_TRANSFER,
                    Outgoing(true),
                    Quantity(1),
                    Recipient("Family")
                )
            )
        )
    }

    @Test
    fun `can post multiple personal transfer transactions`() {
        val handler = postPersonalTransferListHandler { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    PERSONAL_TRANSFER,
                    Outgoing(false),
                    Quantity(1),
                    outbound = Outbound("Current"),
                    inbound = Inbound("Savings")
                )
            )
        )
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 15)),
                    Category("Tech"),
                    Value(BigDecimal("500.00")),
                    Description("Speaker"),
                    PERSONAL_TRANSFER,
                    Outgoing(false),
                    Quantity(1),
                    outbound = Outbound("Current"),
                    inbound = Inbound("Credit")
                )
            )
        )
    }

    @Test
    fun `can post multiple income transactions`() {
        val handler = postIncomeListHandler { database.save(it) }

        val response = handler(
            Request(POST, "/").body(
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

        assertThat(response, hasStatus(OK))
        assertThat(
            database.arguments, hasElement(
                Transaction(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    INCOME,
                    Outgoing(false),
                    Quantity(1),
                    source = Source("Work")
                )
            ).and(
                hasElement(
                    Transaction(
                        Date(LocalDate.of(2020, 10, 15)),
                        Category("Wages"),
                        Value(BigDecimal("500.00")),
                        Description("Wages"),
                        INCOME,
                        Outgoing(false),
                        Quantity(1),
                        source = Source("Work")
                    )
                )
            )
        )
    }
}