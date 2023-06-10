package unit.common

import dao.mongo.Entity
import domain.*
import java.math.BigDecimal
import java.time.LocalDate

class Factory(
    private val date: Date = Date(LocalDate.of(2020, 1, 1)),
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
    private val id: String = "12345"
) {
    fun standingOrder() = StandingOrder(
        date,
        frequency,
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
    )
}
