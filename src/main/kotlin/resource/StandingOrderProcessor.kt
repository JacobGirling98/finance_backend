package resource

import config.logger
import dao.Database
import dao.Entity
import dao.asEntity
import domain.AddedBy
import domain.Date
import domain.Frequency.MONTHLY
import domain.Frequency.WEEKLY
import domain.StandingOrder
import domain.Transaction
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.util.*

class StandingOrderProcessor(
    private val standingOrderDatabase: Database<StandingOrder, UUID>,
    private val transactionsDatabase: Database<Transaction, UUID>,
    private val now: () -> LocalDate
) {
    fun process(standingOrder: Entity<StandingOrder>) {
        if (standingOrder.domain.date.value > now()) {
            return
        }
        var standingOrderToChange: StandingOrder = standingOrder.domain.copy()
        while (standingOrderToChange.date.value <= now()) {
            logger.info { "Standing Order: ${standingOrderToChange.description.value} - ${standingOrderToChange.date.value}" }
            transactionsDatabase.save(standingOrderToChange.toTransaction())
            standingOrderToChange = standingOrderToChange.copy(
                date = when (standingOrderToChange.frequency) {
                    MONTHLY -> Date(standingOrderToChange.date.value.plusMonths(standingOrderToChange.frequencyQuantity.value.toLong()))
                    WEEKLY -> Date(standingOrderToChange.date.value.plusWeeks(standingOrderToChange.frequencyQuantity.value.toLong()))
                }
            )
            standingOrderDatabase.update(standingOrderToChange.asEntity(standingOrder.id) { standingOrder.lastModified })
        }
    }

    fun processAll(standingOrders: List<Entity<StandingOrder>>) {
        logger.info { "Processing standing orders..." }
        standingOrders.forEach { process(it) }
    }

    fun schedule() {
        val now = java.util.Date.from(Instant.now())
        val day = Duration.ofDays(1).toMillis()

        if (transactionsDatabase.selectAll().isEmpty()) {
            logger.info { "There are no transactions, standing orders won't be scheduled. Restart the app after syncing transactions" }
            return
        }

        Timer().scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    processAll(standingOrderDatabase.selectAll())
                }
            },
            now,
            day
        )
    }

    private fun StandingOrder.toTransaction() = Transaction(
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
        AddedBy("standing-order-processor")
    )
}
