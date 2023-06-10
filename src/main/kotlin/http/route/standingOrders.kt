package http.route

import dao.mongo.StandingOrderCollection
import domain.*
import domain.Date
import http.asTag
import http.handler.addStandingOrderHandler
import http.handler.getStandingOrdersHandler
import http.lense.standingOrderLens
import http.lense.standingOrderListLens
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

private const val BASE_URL = "/standing-orders"

fun standingOrdersContract(repository: StandingOrderCollection) = listOf(
    getStandingOrdersContract { repository.findAll() },
    addStandingOrderContract { repository.add(it) }
)

private fun addStandingOrderContract(addStandingOrder: (StandingOrder) -> Unit) = BASE_URL meta {
    operationId = "add a standing order"
    summary = "Interact with standing orders"
    tags += BASE_URL.asTag()
    receiving(standingOrderLens to StandingOrder(
        Date(LocalDate.of(2023, 1, 1)),
        Frequency.MONTHLY,
        Category("Food"),
        Value.of(20.00),
        Description("Shopping"),
        TransactionType.DEBIT,
        Outgoing(true),
        Quantity(1),
        UUID.randomUUID()
    ))
    returning(Status.NO_CONTENT)
} bindContract Method.POST to addStandingOrderHandler(addStandingOrder)

private fun getStandingOrdersContract(standingOrders: () -> List<StandingOrder>) = BASE_URL meta {
    operationId = "Get all standing orders"
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