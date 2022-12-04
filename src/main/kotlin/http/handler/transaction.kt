package http.handler

import domain.Transaction
import domain.TransactionType
import http.assembler.transactionFrom
import http.lense.*
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
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = {request ->
    val numberSaved = save(creditDebitListLens.extract(request).map { transactionFrom(it, transactionType) })
    Response(Status.OK).body(numberSaved.toString())
}

fun postBankTransferListHandler(
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = {request ->
    val numberSaved = save(bankTransferListLens.extract(request).map { transactionFrom(it) })
    Response(Status.OK).body(numberSaved.toString())
}

fun postPersonalTransferListHandler(
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = {request ->
    val numberSaved = save(personalTransferListLens.extract(request).map { transactionFrom(it) })
    Response(Status.OK).body(numberSaved.toString())
}

fun postIncomeListHandler(
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = {request ->
    val numberSaved = save(incomeListLens.extract(request).map { transactionFrom(it) })
    Response(Status.OK).body(numberSaved.toString())
}