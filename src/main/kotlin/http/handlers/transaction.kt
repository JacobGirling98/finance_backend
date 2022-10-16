package http.handlers

import domain.Transaction
import http.assembler.transactionFrom
import http.marshaller.creditMarshaller
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status

fun postCreditTransactionHandler(save: (transaction: Transaction) -> Unit): HttpHandler = {
    save(transactionFrom(creditMarshaller(it.bodyString())))
    Response(Status.OK)
}