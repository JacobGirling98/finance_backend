package resource

import config.logger
import domain.Date
import domain.Frequency.MONTHLY
import domain.Frequency.WEEKLY
import domain.StandingOrder
import domain.Transaction
import dao.Database
import dao.Entity
import dao.asEntity
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
        if (standingOrder.domain.nextDate.value > now()) {
            return
        }
        var standingOrderToChange: StandingOrder = standingOrder.domain.copy()
        while (standingOrderToChange.nextDate.value <= now()) {
            logger.info { "Standing Order: ${standingOrderToChange.description.value} - ${standingOrderToChange.nextDate.value}" }
            transactionsDatabase.save(standingOrderToChange.toTransaction())
            standingOrderToChange = standingOrderToChange.copy(
                nextDate = when (standingOrderToChange.frequency) {
                    MONTHLY -> Date(standingOrderToChange.nextDate.value.plusMonths(1))
                    WEEKLY -> Date(standingOrderToChange.nextDate.value.plusWeeks(1))
                }
            )
            standingOrderDatabase.update(standingOrderToChange.asEntity(standingOrder.id))
        }
    }

    fun processAll(standingOrders: List<Entity<StandingOrder>>) {
        logger.info { "Processing standing orders..." }
        standingOrders.forEach { process(it) }
    }

    fun schedule() {
        val now = java.util.Date.from(Instant.now())
        val day = Duration.ofDays(1).toMillis()

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
        nextDate,
        category,
        value,
        description,
        type,
        outgoing,
        quantity,
        recipient,
        inbound,
        outbound
    )
}