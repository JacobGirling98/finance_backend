package http.model

import domain.Category
import domain.Date
import domain.Description
import domain.Inbound
import domain.Outbound
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.Value
import java.util.UUID

object Transaction {
    data class CreditDebit(
        val date: Date,
        val category: Category,
        val value: Value,
        val description: Description,
        val quantity: Quantity
    )

    data class BankTransfer(
        val date: Date,
        val category: Category,
        val value: Value,
        val description: Description,
        val quantity: Quantity,
        val recipient: Recipient
    )

    data class PersonalTransfer(
        val date: Date,
        val category: Category,
        val value: Value,
        val description: Description,
        val outbound: Outbound,
        val inbound: Inbound
    )

    data class Income(
        val date: Date,
        val category: Category,
        val value: Value,
        val description: Description,
        val source: Source
    )

    data class TransactionConfirmation(
        val transactionCount: Int,
        val value: Float,
        val ids: List<UUID>
    )
}
