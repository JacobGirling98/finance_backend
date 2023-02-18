package http.handler

import domain.Transaction
import domain.TransactionType
import domain.totalValue
import http.assembler.transactionFrom
import http.lense.*
import http.model.TransactionConfirmation
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.with

fun postCreditDebitHandler(
    transactionType: TransactionType,
    save: (transaction: Transaction) -> Unit
): HttpHandler = { request ->
    save(transactionFrom(creditDebitLens.extract(request), transactionType))
    Response(NO_CONTENT)
}

fun postBankTransferHandler(save: (transaction: Transaction) -> Unit): HttpHandler = { request ->
    save(transactionFrom(bankTransferLens.extract(request)))
    Response(NO_CONTENT)
}

fun postPersonalTransferHandler(save: (transaction: Transaction) -> Unit): HttpHandler = { request ->
    save(transactionFrom(personalTransferLens.extract(request)))
    Response(NO_CONTENT)
}

fun postIncomeHandler(save: (transaction: Transaction) -> Unit): HttpHandler = { request ->
    save(transactionFrom(incomeLens.extract(request)))
    Response(NO_CONTENT)
}

fun postCreditDebitListHandler(
    transactionType: TransactionType,
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = { request ->
    val transactions = creditDebitListLens.extract(request).map { transactionFrom(it, transactionType) }
    val numberSaved = save(transactions)
    Response(NO_CONTENT).with(
        transactionConfirmationLens of TransactionConfirmation(
            numberSaved,
            transactions.totalValue()
        )
    )
}

fun postBankTransferListHandler(
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = { request ->
    val transactions = bankTransferListLens.extract(request).map { transactionFrom(it) }
    val numberSaved = save(transactions)
    Response(NO_CONTENT).with(
        transactionConfirmationLens of TransactionConfirmation(
            numberSaved,
            transactions.totalValue()
        )
    )
}

fun postPersonalTransferListHandler(
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = { request ->
    val transactions = personalTransferListLens.extract(request).map { transactionFrom(it) }
    val numberSaved = save(transactions)
    Response(NO_CONTENT).with(
        transactionConfirmationLens of TransactionConfirmation(
            numberSaved,
            transactions.totalValue()
        )
    )
}

fun postIncomeListHandler(
    save: (transaction: List<Transaction>) -> Int
): HttpHandler = { request ->
    val transactions = incomeListLens.extract(request).map { transactionFrom(it) }
    val numberSaved = save(transactions)
    Response(NO_CONTENT).with(
        transactionConfirmationLens of TransactionConfirmation(
            numberSaved,
            transactions.totalValue()
        )
    )
}