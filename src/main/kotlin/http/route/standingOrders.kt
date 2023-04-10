package http.route

import dao.StandingOrdersDatabase
import domain.*
import domain.Date
import http.asTag
import http.handler.getStandingOrdersHandler
import http.lense.standingOrderListLens
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

private const val BASE_URL = "/standing-orders"

fun standingOrdersContract(database: StandingOrdersDatabase) = listOf(
    getStandingOrdersRoute { database.data }
)

private fun getStandingOrdersRoute(standingOrders: () -> MutableList<StandingOrder>) = BASE_URL meta {
    operationId = BASE_URL
    summary = "Interact with standing orders"
    tags += BASE_URL.asTag()
    returning(Status.OK, standingOrderListLens to listOf(
        StandingOrder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            TransactionType.CREDIT,
            Outgoing(false),
            Quantity(1),
            UUID.randomUUID(),
            Recipient("String"),
            Inbound("String"),
            Outbound("String"),
            Source("String")
        )
    ))
} bindContract Method.GET to getStandingOrdersHandler(standingOrders)