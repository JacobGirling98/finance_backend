package http.contract

import domain.*
import domain.Date
import http.asTag
import http.handler.addStandingOrderHandler
import http.handler.getStandingOrdersHandler
import http.lense.standingOrderLens
import http.lense.standingOrderListLens
import dao.Database
import dao.Entity
import dao.entityOf
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

private const val BASE_URL = "/standing-orders"

fun standingOrdersContract(repository: Database<StandingOrder, UUID>) = listOf(
    getStandingOrdersContract { repository.selectAll() },
    addStandingOrderContract { repository.save(it) }
)

private fun addStandingOrderContract(addStandingOrder: (StandingOrder) -> Unit) = BASE_URL meta {
    operationId = "add a standing order"
    summary = "Interact with standing orders"
    tags += BASE_URL.asTag()
    receiving(
        standingOrderLens to
                StandingOrder(
                    Date(LocalDate.of(2023, 1, 1)),
                    Frequency.MONTHLY,
                    Category("Food"),
                    Value.of(20.00),
                    Description("Shopping"),
                    TransactionType.DEBIT,
                    Outgoing(true),
                    Quantity(1),
                )
    )
    returning(Status.NO_CONTENT)
} bindContract Method.POST to addStandingOrderHandler(addStandingOrder)

private fun getStandingOrdersContract(standingOrders: () -> List<Entity<StandingOrder>>) = BASE_URL meta {
    operationId = "Get all standing orders"
    summary = "Interact with standing orders"
    tags += BASE_URL.asTag()
    returning(
        Status.OK, standingOrderListLens to listOf(
            entityOf(
                StandingOrder(
                    Date(LocalDate.of(2023, 1, 1)),
                    Frequency.MONTHLY,
                    Category("String"),
                    Value(BigDecimal.ZERO),
                    Description("String"),
                    TransactionType.CREDIT,
                    Outgoing(false),
                    Quantity(1),
                    Recipient("String"),
                    Inbound("String"),
                    Outbound("String")
                )
            )
        )
    )
} bindContract Method.GET to getStandingOrdersHandler(standingOrders)