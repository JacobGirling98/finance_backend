package http.handler

import dao.mongo.Entity
import domain.StandingOrder
import http.lense.standingOrderLens
import http.lense.standingOrderListLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with


fun getStandingOrdersHandler(standingOrders: () -> List<Entity<StandingOrder>>): HttpHandler = {
    Response(Status.OK).with(standingOrderListLens of standingOrders())
}

fun addStandingOrderHandler(addStandingOrder: (StandingOrder) -> Unit): HttpHandler = { request ->
    addStandingOrder(standingOrderLens(request))
    Response(Status.NO_CONTENT)
}