package resource

import config.logger
import dao.StandingOrdersDatabase
import dao.TransactionsDatabase
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
    private val standingOrdersDatabase: StandingOrdersDatabase,
    private val transactionsDatabase: TransactionsDatabase,
    private val now: () -> LocalDate
) {
    fun process(standingOrder: StandingOrder) {
        if (standingOrder.nextDate.value > now()) {
            return
        }
        var standingOrderToChange: StandingOrder = standingOrder.copy()
        while (standingOrderToChange.nextDate.value <= now()) {
            logger.info { "Standing Order: ${standingOrderToChange.description.value} - ${standingOrderToChange.nextDate.value}" }
            transactionsDatabase.save(standingOrderToChange.toTransaction())
            standingOrderToChange = standingOrderToChange.copy(
                nextDate = when (standingOrderToChange.frequency) {
                    MONTHLY -> Date(standingOrderToChange.nextDate.value.plusMonths(1))
                    WEEKLY -> Date(standingOrderToChange.nextDate.value.plusWeeks(1))
                }
            )
            standingOrdersDatabase.update(standingOrderToChange)
        }
    }

    fun processAll(standingOrders: List<StandingOrder>) {
        logger.info { "Processing standing orders..." }
        standingOrders.forEach { process(it) }
        standingOrdersDatabase.flush()
    }

    fun schedule() {
        val now = java.util.Date.from(Instant.now())
        val day = Duration.ofDays(1).toMillis()

        Timer().scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    standingOrdersDatabase.read()
                    processAll(standingOrdersDatabase.data)
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
        outbound,
        source
    )
}