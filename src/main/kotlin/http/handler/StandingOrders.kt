package http.handler

import dao.Entity
import domain.StandingOrder
import domain.TransactionType
import http.assembler.map
import http.assembler.standingOrderFrom
import http.lense.bankTransferStandingOrderLens
import http.lense.creditDebitStandingOrderLens
import http.lense.entityBankTransferStandingOrderLens
import http.lense.entityCreditDebitStandingOrderLens
import http.lense.entityIncomeStandingOrderLens
import http.lense.entityPersonalTransferStandingOrderLens
import http.lense.incomeStandingOrderLens
import http.lense.personalTransferStandingOrderLens
import http.lense.standingOrderListLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.with
import java.util.*

fun getStandingOrdersHandler(standingOrders: () -> List<Entity<StandingOrder>>): HttpHandler = {
    Response(Status.OK).with(standingOrderListLens of standingOrders())
}

fun postCreditDebitStandingOrderHandler(
    transactionType: TransactionType,
    addStandingOrder: (StandingOrder) -> UUID
): HttpHandler = { request ->
    addStandingOrder(standingOrderFrom(creditDebitStandingOrderLens(request), transactionType))
    Response(NO_CONTENT)
}

fun postBankTransferStandingOrderHandler(addStandingOrder: (StandingOrder) -> UUID): HttpHandler = { request ->
    addStandingOrder(standingOrderFrom(bankTransferStandingOrderLens(request)))
    Response(NO_CONTENT)
}

fun postPersonalTransferStandingOrderHandler(addStandingOrder: (StandingOrder) -> UUID): HttpHandler = { request ->
    addStandingOrder(standingOrderFrom(personalTransferStandingOrderLens(request)))
    Response(NO_CONTENT)
}

fun postIncomeStandingOrderHandler(addStandingOrder: (StandingOrder) -> UUID): HttpHandler = { request ->
    addStandingOrder(standingOrderFrom(incomeStandingOrderLens(request)))
    Response(NO_CONTENT)
}

fun putCreditDebitStandingOrderHandler(
    transactionType: TransactionType,
    updateStandingOrder: (Entity<StandingOrder>) -> Unit
): HttpHandler = { request ->
    updateStandingOrder(
        entityCreditDebitStandingOrderLens(request).map { model ->
            standingOrderFrom(
                model,
                transactionType
            )
        }
    )
    Response(NO_CONTENT)
}

fun putBankTransferStandingOrderHandler(
    updateStandingOrder: (Entity<StandingOrder>) -> Unit
): HttpHandler = { request ->
    updateStandingOrder(
        entityBankTransferStandingOrderLens(request).map { model ->
            standingOrderFrom(model)
        }
    )
    Response(NO_CONTENT)
}

fun putPersonalTransferStandingOrderHandler(
    updateStandingOrder: (Entity<StandingOrder>) -> Unit
): HttpHandler = { request ->
    updateStandingOrder(
        entityPersonalTransferStandingOrderLens(request).map { model ->
            standingOrderFrom(model)
        }
    )
    Response(NO_CONTENT)
}

fun putIncomeStandingOrderHandler(
    updateStandingOrder: (Entity<StandingOrder>) -> Unit
): HttpHandler = { request ->
    updateStandingOrder(
        entityIncomeStandingOrderLens(request).map { model ->
            standingOrderFrom(model)
        }
    )
    Response(NO_CONTENT)
}
