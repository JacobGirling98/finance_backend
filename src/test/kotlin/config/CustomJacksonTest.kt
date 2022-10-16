package config

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import domain.*
import http.marshaller.bankTransferMarshaller
import http.marshaller.creditDebitMarshaller
import http.marshaller.incomeMarshaller
import http.marshaller.personalTransferMarshaller
import http.models.BankTransfer
import http.models.CreditDebit
import http.models.Income
import http.models.PersonalTransfer
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class CustomJacksonTest {

    @Test
    fun `can convert credit-debit json to credit dto`() {
        assertThat(
            creditDebitMarshaller(
                """
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "quantity": 2
                    }
                """.trimIndent()
            ), equalTo(
                CreditDebit(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    Quantity(2)
                )
            )
        )
    }

    @Test
    fun `can convert bank transfer to bank transfer dto`() {
        assertThat(
            bankTransferMarshaller(
                """
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "quantity": 2,
                        "recipient": "Friend"
                    }
                """.trimIndent()
            ),
            equalTo(
                BankTransfer(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    Quantity(2),
                    Recipient("Friend")
                )
            )
        )
    }

    @Test
    fun `can convert personal transfer to personal transfer dto`() {
        assertThat(
            personalTransferMarshaller(
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
            ),
            equalTo(
                PersonalTransfer(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    Outbound("Current"),
                    Inbound("Savings")
                )
            )
        )
    }

    @Test
    fun `can convert income transaction to income dto`() {
        assertThat(
            incomeMarshaller(
                """
                    {
                        "date": "2020-10-12",
                        "category": "Food",
                        "value": 12.50,
                        "description": "Cake",
                        "source": "Work"
                    }
                """.trimIndent()
            ),
            equalTo(
                Income(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    Source("Work")
                )
            )
        )
    }
}