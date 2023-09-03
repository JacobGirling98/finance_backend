package http.model

import domain.Category
import domain.Date
import domain.Description
import domain.Frequency
import domain.FrequencyQuantity
import domain.Inbound
import domain.Outbound
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.Value

object StandingOrder {
    data class CreditDebit(
        val nextDate: Date,
        val frequencyQuantity: FrequencyQuantity,
        val frequencyUnit: Frequency,
        val category: Category,
        val value: Value,
        val description: Description,
        val quantity: Quantity
    )

    data class BankTransfer(
        val nextDate: Date,
        val frequencyQuantity: FrequencyQuantity,
        val frequencyUnit: Frequency,
        val category: Category,
        val value: Value,
        val description: Description,
        val quantity: Quantity,
        val recipient: Recipient
    )

    data class PersonalTransfer(
        val nextDate: Date,
        val frequencyQuantity: FrequencyQuantity,
        val frequencyUnit: Frequency,
        val category: Category,
        val value: Value,
        val description: Description,
        val outbound: Outbound,
        val inbound: Inbound
    )

    data class Income(
        val nextDate: Date,
        val frequencyQuantity: FrequencyQuantity,
        val frequencyUnit: Frequency,
        val category: Category,
        val value: Value,
        val description: Description,
        val source: Source
    )
}
