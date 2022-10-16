package http.routes

import dao.Database
import domain.TransactionType.CREDIT
import domain.TransactionType.DEBIT
import http.handlers.postBankTransferHandler
import http.handlers.postCreditDebitHandler
import http.handlers.postIncomeHandler
import http.handlers.postPersonalTransferHandler
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

fun transactionRoutes(database: Database) = routes(
    "/transaction" bind routes(
        "/credit" bind POST to postCreditDebitHandler(CREDIT) { database.save(it) },
        "/debit" bind POST to postCreditDebitHandler(DEBIT) { database.save(it) },
        "/bank-transfer" bind POST to postBankTransferHandler { database.save(it) },
        "/personal-transfer" bind POST to postPersonalTransferHandler { database.save(it) },
        "/income" bind POST to postIncomeHandler { database.save(it) }
    )
)