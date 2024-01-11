package http.handler

import dao.Entity
import dao.Page
import domain.PageNumber
import domain.PageSize
import domain.Transaction
import domain.TransactionType
import domain.totalValue
import domain.transactionTypeFrom
import http.assembler.map
import http.assembler.transactionFrom
import http.lense.bankTransferLens
import http.lense.bankTransferListLens
import http.lense.creditDebitLens
import http.lense.creditDebitListLens
import http.lense.entityBankTransferLens
import http.lense.entityCreditDebitLens
import http.lense.entityIncomeLens
import http.lense.entityPersonalTransferLens
import http.lense.idQuery
import http.lense.incomeLens
import http.lense.incomeListLens
import http.lense.optionalEndDateQuery
import http.lense.optionalStartDateQuery
import http.lense.optionalTransactionTypeStringQuery
import http.lense.pageNumberQuery
import http.lense.pageSizeQuery
import http.lense.personalTransferLens
import http.lense.personalTransferListLens
import http.lense.searchTermQuery
import http.lense.transactionConfirmationLens
import http.lense.transactionPageLens
import http.model.Transaction.TransactionConfirmation
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import resource.toFilter
import java.util.*

fun postCreditDebitHandler(
    transactionType: TransactionType,
    save: (transaction: Transaction) -> UUID
): HttpHandler = { request ->
    save(transactionFrom(creditDebitLens.extract(request), transactionType, request.userHeader()))
    Response(NO_CONTENT)
}

fun postBankTransferHandler(save: (transaction: Transaction) -> UUID): HttpHandler = { request ->
    save(transactionFrom(bankTransferLens.extract(request), request.userHeader()))
    Response(NO_CONTENT)
}

fun postPersonalTransferHandler(save: (transaction: Transaction) -> UUID): HttpHandler = { request ->
    save(transactionFrom(personalTransferLens.extract(request), request.userHeader()))
    Response(NO_CONTENT)
}

fun postIncomeHandler(save: (transaction: Transaction) -> UUID): HttpHandler = { request ->
    save(transactionFrom(incomeLens.extract(request), request.userHeader()))
    Response(NO_CONTENT)
}

fun postCreditDebitListHandler(
    transactionType: TransactionType,
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions =
        creditDebitListLens.extract(request).map { transactionFrom(it, transactionType, request.userHeader()) }
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
    val transactions = bankTransferListLens.extract(request).map { transactionFrom(it, request.userHeader()) }
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
    val transactions = personalTransferListLens.extract(request).map { transactionFrom(it, request.userHeader()) }
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
    val transactions = incomeListLens.extract(request).map { transactionFrom(it, request.userHeader()) }
    val ids = save(transactions)
    Response(OK).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue()
        )
    )
}

fun paginatedTransactionsHandler(
    selectAll: (pageNumber: PageNumber, pageSize: PageSize) -> Page<Entity<Transaction>>,
    selectBy: (pageNumber: PageNumber, pageSize: PageSize, filter: (Entity<Transaction>) -> Boolean) -> Page<Entity<Transaction>>
): HttpHandler = { request ->
    val pageNumber = pageNumberQuery.extract(request)
    val pageSize = pageSizeQuery.extract(request)

    val startDate = optionalStartDateQuery.extract(request)
    val endDate = optionalEndDateQuery.extract(request)
    val type = optionalTransactionTypeStringQuery.extract(request)?.let { transactionTypeFrom(it) }

    val selectFn: () -> Page<Entity<Transaction>> = if (startDate == null && endDate == null && type == null) {
        { selectAll(pageNumber, pageSize) }
    } else {
        { selectBy(pageNumber, pageSize, toFilter(endDate, startDate, type)) }
    }

    val data = selectFn()
    Response(OK).with(transactionPageLens of data)
}

fun searchTransactionsHandler(
    search: (term: String, pageNumber: PageNumber, pageSize: PageSize) -> Page<Entity<Transaction>>
): HttpHandler = { request ->
    val searchTerm = searchTermQuery.extract(request)
    val pageNumber = pageNumberQuery.extract(request)
    val pageSize = pageSizeQuery.extract(request)
    val results = search(searchTerm, pageNumber, pageSize)
    Response(OK).with(transactionPageLens of results)
}

fun putCreditDebitTransactionHandler(
    transactionType: TransactionType,
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = { request ->
    updateTransaction(
        entityCreditDebitLens(request).map { model ->
            transactionFrom(
                model,
                transactionType,
                request.userHeader()
            )
        }
    )
    Response(NO_CONTENT)
}

fun putBankTransferTransactionHandler(
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = { request ->
    updateTransaction(
        entityBankTransferLens(request).map { model ->
            transactionFrom(model, request.userHeader())
        }
    )
    Response(NO_CONTENT)
}

fun putPersonalTransferTransactionHandler(
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = { request ->
    updateTransaction(
        entityPersonalTransferLens(request).map { model ->
            transactionFrom(model, request.userHeader())
        }
    )
    Response(NO_CONTENT)
}

fun putIncomeTransactionHandler(
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = { request ->
    updateTransaction(
        entityIncomeLens(request).map { model ->
            transactionFrom(model, request.userHeader())
        }
    )
    Response(NO_CONTENT)
}

fun deleteEntityHandler(delete: (UUID) -> Unit): HttpHandler = { request ->
    val id = idQuery.extract(request)
    delete(id)
    Response(NO_CONTENT)
}
