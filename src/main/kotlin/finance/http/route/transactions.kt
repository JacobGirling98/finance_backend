package finance.http.route

import finance.dao.Database
import finance.domain.Transaction
import finance.domain.TransactionType.CREDIT
import finance.domain.TransactionType.DEBIT
import finance.http.handler.*
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

fun transactionRoutes(database: Database<Transaction>) = routes(
    "/transaction" bind routes(
        "/credit" bind POST to postCreditDebitHandler(CREDIT) { database.save(it) },
        "/debit" bind POST to postCreditDebitHandler(DEBIT) { database.save(it) },
        "/bank-transfer" bind POST to postBankTransferHandler { database.save(it) },
        "/personal-transfer" bind POST to postPersonalTransferHandler { database.save(it) },
        "/income" bind POST to postIncomeHandler { database.save(it) },
        "/multiple" bind routes(
            "/credit" bind POST to postCreditDebitListHandler(CREDIT) { database.save(it) },
            "/debit" bind POST to postCreditDebitListHandler(DEBIT) { database.save(it) },
            "/bank-transfer" bind POST to postBankTransferListHandler { database.save(it) },
            "/personal-transfer" bind POST to postPersonalTransferListHandler { database.save(it) },
            "/income" bind POST to postIncomeListHandler { database.save(it) },
        )
    )
)