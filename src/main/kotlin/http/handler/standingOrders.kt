package http.handler

import domain.StandingOrder
import http.lense.standingOrderListLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with


fun getStandingOrdersHandler(standingOrders: () -> List<StandingOrder>): HttpHandler = {
    Response(Status.OK).with(standingOrderListLens of standingOrders())
}