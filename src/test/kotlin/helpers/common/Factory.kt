package helpers.common

import dao.Entity
import domain.AddedBy
import domain.Category
import domain.Date
import domain.Description
import domain.Frequency
import domain.FrequencyQuantity
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.StandingOrder
import domain.Transaction
import domain.TransactionType
import domain.Value
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Factory(
    private val date: Date = Date(LocalDate.of(2020, 1, 1)),
    private val frequencyQuantity: FrequencyQuantity = FrequencyQuantity(1),
    private val frequency: Frequency = Frequency.MONTHLY,
    private val category: Category = Category("Food"),
    private val value: Value = Value(BigDecimal("20.00")),
    private val description: Description = Description("Milk"),
    private val type: TransactionType = TransactionType.BANK_TRANSFER,
    private val outgoing: Outgoing = Outgoing(true),
    private val quantity: Quantity = Quantity(1),
    private val recipient: Recipient? = null,
    private val inbound: Inbound? = null,
    private val outbound: Outbound? = null,
    private val source: Source? = null,
    private val id: UUID = UUID.randomUUID(),
    private val addedBy: AddedBy = AddedBy("Jacob"),
    private val now: () -> LocalDateTime = { LocalDateTime.now() }
) {
    fun standingOrder() = StandingOrder(
        date = date,
        frequencyQuantity = frequencyQuantity,
        frequency = frequency,
        category = category,
        value = value,
        description = description,
        type = type,
        outgoing = outgoing,
        quantity = quantity,
        recipient = recipient,
        inbound = inbound,
        outbound = outbound,
        source = source
    )

    fun standingOrderEntity() = Entity(id, standingOrder())

    fun transaction() = Transaction(
        date,
        category,
        value,
        description,
        type,
        outgoing,
        quantity,
        recipient,
        inbound,
        outbound,
        source,
        addedBy
    )
}
