package http.handler

import domain.Transaction
import domain.TransactionType
import domain.totalValue
import http.assembler.transactionFrom
import http.lense.bankTransferLens
import http.lense.bankTransferListLens
import http.lense.creditDebitLens
import http.lense.creditDebitListLens
import http.lense.incomeLens
import http.lense.incomeListLens
import http.lense.personalTransferLens
import http.lense.personalTransferListLens
import http.lense.transactionConfirmationLens
import http.model.TransactionConfirmation
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import java.util.*

fun postCreditDebitHandler(
    transactionType: TransactionType,
    save: (transaction: Transaction) -> UUID
): HttpHandler = { request ->
    save(transactionFrom(creditDebitLens.extract(request), transactionType))
    Response(NO_CONTENT)
}

fun postBankTransferHandler(save: (transaction: Transaction) -> UUID): HttpHandler = { request ->
    save(transactionFrom(bankTransferLens.extract(request)))
    Response(NO_CONTENT)
}

fun postPersonalTransferHandler(save: (transaction: Transaction) -> UUID): HttpHandler = { request ->
    save(transactionFrom(personalTransferLens.extract(request)))
    Response(NO_CONTENT)
}

fun postIncomeHandler(save: (transaction: Transaction) -> UUID): HttpHandler = { request ->
    save(transactionFrom(incomeLens.extract(request)))
    Response(NO_CONTENT)
}

fun postCreditDebitListHandler(
    transactionType: TransactionType,
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions = creditDebitListLens.extract(request).map { transactionFrom(it, transactionType) }
    val ids = save(transactions)
    Response(OK).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue()
        )
    )
}

fun postBankTransferListHandler(
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions = bankTransferListLens.extract(request).map { transactionFrom(it) }
    val ids = save(transactions)
    Response(OK).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue()
        )
    )
}

fun postPersonalTransferListHandler(
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions = personalTransferListLens.extract(request).map { transactionFrom(it) }
    val ids = save(transactions)
    Response(OK).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue()
        )
    )
}

fun postIncomeListHandler(
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions = incomeListLens.extract(request).map { transactionFrom(it) }
    val ids = save(transactions)
    Response(OK).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue()
        )
    )
}
