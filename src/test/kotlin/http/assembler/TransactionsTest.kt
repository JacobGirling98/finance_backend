package http.assembler

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import domain.*
import http.models.Credit
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class TransactionsTest {

    @Test
    fun `convert credit dto to transaction`() {
        assertThat(
            transactionFrom(Credit(
                Date(LocalDate.MIN),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Grapes"),
                Quantity(1)
            )),
            equalTo(Transaction(
                Date(LocalDate.MIN),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Grapes"),
                TransactionType.CREDIT,
                Outgoing(true),
                Quantity(1)
            ))
        )
    }
}