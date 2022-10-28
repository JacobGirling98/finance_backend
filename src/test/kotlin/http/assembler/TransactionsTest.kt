package http.assembler

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import domain.*
import domain.TransactionType.*
import http.model.BankTransfer
import http.model.CreditDebit
import http.model.Income
import http.model.PersonalTransfer
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class TransactionsTest {

    @Test
    fun `convert credit dto to transaction`() {
        assertThat(
            transactionFrom(
                CreditDebit(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
                    Quantity(1)
                ), CREDIT
            ),
            equalTo(
                Transaction(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
                    CREDIT,
                    Outgoing(true),
                    Quantity(1)
                )
            )
        )
    }

    @Test
    fun `convert bank transfer dto to transaction`() {
        assertThat(
            transactionFrom(
                BankTransfer(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
                    Quantity(1),
                    Recipient("Friend")
                )
            ),
            equalTo(
                Transaction(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
                    BANK_TRANSFER,
                    Outgoing(true),
                    Quantity(1),
                    recipient = Recipient("Friend")
                )
            )
        )
    }

    @Test
    fun `convert personal transfer dto to transaction`() {
        assertThat(
            transactionFrom(
                PersonalTransfer(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
                    Outbound("Current"),
                    Inbound("Savings")
                )
            ),
            equalTo(
                Transaction(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
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
    fun `convert income dto to transaction`() {
        assertThat(
            transactionFrom(
                Income(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
                    Source("Work")
                )
            ),
            equalTo(
                Transaction(
                    Date(LocalDate.MIN),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Grapes"),
                    INCOME,
                    Outgoing(false),
                    Quantity(1),
                    source = Source("Work")
                )
            )
        )
    }
}