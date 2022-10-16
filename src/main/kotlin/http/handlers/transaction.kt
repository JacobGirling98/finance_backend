package http.handlers

import domain.Transaction
import domain.TransactionType.CREDIT
import http.assembler.transactionFrom
import http.marshaller.creditDebitMarshaller
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status

fun postCreditTransactionHandler(save: (transaction: Transaction) -> Unit): HttpHandler = {
    save(transactionFrom(creditDebitMarshaller(it.bodyString()), CREDIT))
    Response(Status.OK)
}