package config

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import domain.*
import http.marshaller.creditMarshaller
import http.models.Credit
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class CustomJacksonTest {

    @Test
    fun `can convert credit json to credit dto`() {
        assertThat(
            creditMarshaller(
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
                Credit(
                    Date(LocalDate.of(2020, 10, 12)),
                    Category("Food"),
                    Value(BigDecimal("12.50")),
                    Description("Cake"),
                    Quantity(2)
                )
            )
        )
    }
}