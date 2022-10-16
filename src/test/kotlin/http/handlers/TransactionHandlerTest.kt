package http.handlers

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.hasElement
import dao.Database
import domain.*
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

private class TestDatabase : Database {

    var arguments = mutableListOf<Transaction>()

    override fun save(transaction: Transaction) {
        arguments.add(transaction)
    }

}

class TransactionHandlerTest {

    private val database = TestDatabase()

    @Test
    fun `can post a credit transaction`() {
        val handler = postCreditTransactionHandler { database.save(it) }

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
        assertThat(database.arguments, hasElement(Transaction(
            Date(LocalDate.of(2020, 10, 12)),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Cake"),
            TransactionType.CREDIT,
            Outgoing(true),
            Quantity(2)
        )))
    }
}