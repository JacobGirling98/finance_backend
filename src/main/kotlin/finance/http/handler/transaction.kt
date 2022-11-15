package finance.http.handler

import finance.domain.Transaction
import finance.domain.TransactionType
import finance.http.assembler.transactionFrom
import finance.http.lense.*
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status

fun postCreditDebitHandler(
    transactionType: TransactionType,
    save: (transaction: Transaction) -> Unit
): HttpHandler = { request ->
    save(transactionFrom(creditDebitLens.extract(request), transactionType))
    Response(Status.OK)
}

fun postBankTransferHandler(save: (transaction: Transaction) -> Unit): HttpHandler = { request ->
    save(transactionFrom(bankTransferLens.extract(request)))
    Response(Status.OK)
}

fun postPersonalTransferHandler(save: (transaction: Transaction) -> Unit): HttpHandler = { request ->
    save(transactionFrom(personalTransferLens.extract(request)))
    Response(Status.OK)
}

fun postIncomeHandler(save: (transaction: Transaction) -> Unit): HttpHandler = { request ->
    save(transactionFrom(incomeLens.extract(request)))
    Response(Status.OK)
}

fun postCreditDebitListHandler(
    transactionType: TransactionType,
    save: (transaction: List<Transaction>) -> Unit
): HttpHandler = { request ->
    save(creditDebitListLens.extract(request).map { transactionFrom(it, transactionType) })
    Response(Status.OK)
}

fun postBankTransferListHandler(
    save: (transaction: List<Transaction>) -> Unit
): HttpHandler = { request ->
    save(bankTransferListLens.extract(request).map { transactionFrom(it) })
    Response(Status.OK)
}

fun postPersonalTransferListHandler(
    save: (transaction: List<Transaction>) -> Unit
): HttpHandler = { request ->
    save(personalTransferListLens.extract(request).map { transactionFrom(it) })
    Response(Status.OK)
}

fun postIncomeListHandler(
    save: (transaction: List<Transaction>) -> Unit
): HttpHandler = { request ->
    save(incomeListLens.extract(request).map { transactionFrom(it) })
    Response(Status.OK)
}