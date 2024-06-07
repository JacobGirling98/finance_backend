package http.handler

import dao.AuditableEntity
import dao.Entity
import dao.Page
import dao.asEntity
import domain.AddedBy
import domain.PageNumber
import domain.PageSize
import domain.Transaction
import domain.TransactionType
import domain.totalValue
import domain.transactionTypeFrom
import http.assembler.transactionFrom
import http.lense.bankTransferLens
import http.lense.bankTransferListLens
import http.lense.creditDebitLens
import http.lense.creditDebitListLens
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
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import resource.toFilter
import java.util.*

fun postCreditDebitListHandler(
    transactionType: TransactionType,
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions =
        creditDebitListLens.extract(request).map { transactionFrom(it, transactionType, request.userHeader()) }
    val ids = save(transactions)
    Response(CREATED).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue(),
            ids
        )
    )
}

fun postBankTransferListHandler(
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions = bankTransferListLens.extract(request).map { transactionFrom(it, request.userHeader()) }
    val ids = save(transactions)
    Response(CREATED).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue(),
            ids
        )
    )
}

fun postPersonalTransferListHandler(
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions = personalTransferListLens.extract(request).map { transactionFrom(it, request.userHeader()) }
    val ids = save(transactions)
    Response(CREATED).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue(),
            ids
        )
    )
}

fun postIncomeListHandler(
    save: (transaction: List<Transaction>) -> List<UUID>
): HttpHandler = { request ->
    val transactions = incomeListLens.extract(request).map { transactionFrom(it, request.userHeader()) }
    val ids = save(transactions)
    Response(CREATED).with(
        transactionConfirmationLens of TransactionConfirmation(
            ids.size,
            transactions.totalValue(),
            ids
        )
    )
}

fun paginatedTransactionsHandler(
    selectAll: (pageNumber: PageNumber, pageSize: PageSize) -> Page<AuditableEntity<Transaction>>,
    selectBy: (pageNumber: PageNumber, pageSize: PageSize, filter: (AuditableEntity<Transaction>) -> Boolean) -> Page<AuditableEntity<Transaction>>
): HttpHandler = { request ->
    val pageNumber = pageNumberQuery.extract(request)
    val pageSize = pageSizeQuery.extract(request)

    val startDate = optionalStartDateQuery.extract(request)
    val endDate = optionalEndDateQuery.extract(request)
    val type = optionalTransactionTypeStringQuery.extract(request)?.let { transactionTypeFrom(it) }

    val selectFn: () -> Page<AuditableEntity<Transaction>> = if (startDate == null && endDate == null && type == null) {
        { selectAll(pageNumber, pageSize) }
    } else {
        { selectBy(pageNumber, pageSize, toFilter(endDate, startDate, type)) }
    }

    val data = selectFn()
    Response(OK).with(transactionPageLens of data)
}

fun searchTransactionsHandler(
    search: (term: String, pageNumber: PageNumber, pageSize: PageSize) -> Page<AuditableEntity<Transaction>>
): HttpHandler = { request ->
    val searchTerm = searchTermQuery.extract(request)
    val pageNumber = pageNumberQuery.extract(request)
    val pageSize = pageSizeQuery.extract(request)
    val results = search(searchTerm, pageNumber, pageSize)
    Response(OK).with(transactionPageLens of results)
}

fun putCreditDebitTransactionHandler(
    transactionType: TransactionType,
    id: String,
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = putTransactionHandler(id, updateTransaction, creditDebitLens) { model, user ->
    transactionFrom(
        model,
        transactionType,
        user
    )
}

fun putBankTransferTransactionHandler(
    id: String,
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = putTransactionHandler(id, updateTransaction, bankTransferLens, ::transactionFrom)

fun putPersonalTransferTransactionHandler(
    id: String,
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = putTransactionHandler(id, updateTransaction, personalTransferLens, ::transactionFrom)

fun putIncomeTransactionHandler(
    id: String,
    updateTransaction: (Entity<Transaction>) -> Unit
): HttpHandler = putTransactionHandler(id, updateTransaction, incomeLens, ::transactionFrom)

fun deleteEntityHandler(delete: (UUID) -> Unit): HttpHandler = { request ->
    val id = idQuery.extract(request)
    delete(id)
    Response(NO_CONTENT)
}

private fun <T> putTransactionHandler(
    id: String,
    updateTransaction: (Entity<Transaction>) -> Unit,
    lens: (Request) -> T,
    toDomain: (T, AddedBy) -> Transaction
): HttpHandler = { request ->
    val transaction = lens(request)
    val user = request.userHeader()
    val entity = toDomain(transaction, user).asEntity(UUID.fromString(id))
    updateTransaction(entity)
    Response(NO_CONTENT)
}
