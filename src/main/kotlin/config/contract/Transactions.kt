package config.contract

import dao.AuditableEntity
import dao.Database
import dao.Entity
import dao.Page
import dao.asRandomEntity
import dao.entityOf
import domain.AddedBy
import domain.Category
import domain.Date
import domain.Description
import domain.HasNextPage
import domain.HasPreviousPage
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.PageNumber
import domain.PageSize
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.TotalElements
import domain.TotalPages
import domain.Transaction
import domain.TransactionType.CREDIT
import domain.TransactionType.DEBIT
import domain.Value
import http.asTag
import http.handler.deleteEntityHandler
import http.handler.paginatedTransactionsHandler
import http.handler.postBankTransferHandler
import http.handler.postBankTransferListHandler
import http.handler.postCreditDebitHandler
import http.handler.postCreditDebitListHandler
import http.handler.postIncomeHandler
import http.handler.postIncomeListHandler
import http.handler.postPersonalTransferHandler
import http.handler.postPersonalTransferListHandler
import http.handler.putBankTransferTransactionHandler
import http.handler.putCreditDebitTransactionHandler
import http.handler.putIncomeTransactionHandler
import http.handler.putPersonalTransferTransactionHandler
import http.handler.searchTransactionsHandler
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
import http.model.Transaction.BankTransfer
import http.model.Transaction.CreditDebit
import http.model.Transaction.Income
import http.model.Transaction.PersonalTransfer
import http.model.Transaction.TransactionConfirmation
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import resource.TransactionProcessor
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

private const val BASE_URL = "/transaction"
private const val MULTIPLE_URL = "$BASE_URL/multiple"
private val tag = BASE_URL.asTag()
private val multipleTag = MULTIPLE_URL.asTag()

fun transactionContracts(database: Database<Transaction, UUID>, processor: TransactionProcessor) = listOf(
    postCreditContract { database.save(it) },
    multipleCreditContract { database.save(it) },
    postDebitContract { database.save(it) },
    multipleDebitContract { database.save(it) },
    postBankTransferContract { database.save(it) },
    multipleBankTransferContract { database.save(it) },
    personalTransferRouteContract { database.save(it) },
    multiplePersonalTransferContract { database.save(it) },
    postIncomeContract { database.save(it) },
    multipleIncomeContract { database.save(it) },
    getPaginatedDataRoute(processor::selectAll, processor::selectBy),
    getPaginatedSearchDataRoute { term, pageNumber, pageSize -> processor.search(term, pageNumber, pageSize) },
    putDebitContract { database.update(it) },
    putCreditContract { database.update(it) },
    putBankTransferContract { database.update(it) },
    putPersonalTransferContract { database.update(it) },
    putIncomeContract { database.update(it) },
    deleteContract { database.delete(it) }
)

private fun getPaginatedDataRoute(
    selectAll: (pageNumber: PageNumber, pageSize: PageSize) -> Page<AuditableEntity<Transaction>>,
    selectBy: (pageNumber: PageNumber, pageSize: PageSize, filter: (AuditableEntity<Transaction>) -> Boolean) -> Page<AuditableEntity<Transaction>>
) = BASE_URL meta {
    operationId = BASE_URL
    summary = "Get paginated transactions"
    tags += tag
    queries += pageNumberQuery
    queries += pageSizeQuery
    queries += optionalStartDateQuery
    queries += optionalEndDateQuery
    queries += optionalTransactionTypeStringQuery
    returning(
        OK,
        transactionPageLens to Page(
            listOf(
                entityOf(
                    Transaction(
                        date = Date(LocalDate.of(2023, 1, 1)),
                        Category("String"),
                        Value(BigDecimal.ZERO),
                        Description("String"),
                        CREDIT,
                        Outgoing(true),
                        Quantity(1),
                        Recipient("Nullable String"),
                        Inbound("Nullable String"),
                        Outbound("Nullable String"),
                        Source("Nullable String"),
                        AddedBy("Jacob")
                    )
                )
            ),
            PageNumber(1),
            PageSize(5),
            TotalElements(20),
            TotalPages(4),
            HasPreviousPage(false),
            HasNextPage(true)
        )

    )
} bindContract GET to paginatedTransactionsHandler(selectAll, selectBy)

private fun getPaginatedSearchDataRoute(
    search: (term: String, pageNumber: PageNumber, pageSize: PageSize) -> Page<AuditableEntity<Transaction>>
) = "$BASE_URL/search" meta {
    operationId = "$BASE_URL/search"
    summary = "Search for transactions"
    tags += "$BASE_URL/search".asTag()
    queries += pageNumberQuery
    queries += pageSizeQuery
    queries += searchTermQuery
    returning(
        OK,
        transactionPageLens to Page(
            listOf(
                entityOf(
                    Transaction(
                        date = Date(LocalDate.of(2023, 1, 1)),
                        Category("String"),
                        Value(BigDecimal.ZERO),
                        Description("String"),
                        CREDIT,
                        Outgoing(true),
                        Quantity(1),
                        Recipient("Nullable String"),
                        Inbound("Nullable String"),
                        Outbound("Nullable String"),
                        Source("Nullable String"),
                        AddedBy("Jacob")
                    )
                )
            ),
            PageNumber(1),
            PageSize(5),
            TotalElements(20),
            TotalPages(4),
            HasPreviousPage(false),
            HasNextPage(true)
        )

    )
} bindContract GET to searchTransactionsHandler(search)

private fun postCreditContract(save: (Transaction) -> UUID) = "$BASE_URL/credit" meta {
    operationId = "$BASE_URL/credit"
    summary = "Post a credit transaction"
    tags += "$BASE_URL/credit".asTag()
    receiving(
        creditDebitLens to CreditDebit(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        )
    )
    returning(CREATED to UUID.randomUUID().toString())
} bindContract POST to postCreditDebitHandler(CREDIT, save)

private fun multipleCreditContract(save: (List<Transaction>) -> List<UUID>) = "$MULTIPLE_URL/credit" meta {
    operationId = "$MULTIPLE_URL/credit"
    summary = "Post multiple credit transactions"
    tags += multipleTag
    receiving(
        creditDebitListLens to listOf(
            CreditDebit(
                date = Date(LocalDate.of(2020, 1, 1)),
                Category("String"),
                Value(BigDecimal.ZERO),
                Description("String"),
                Quantity(1)
            )
        )
    )
    returning(NO_CONTENT)
} bindContract POST to postCreditDebitListHandler(CREDIT, save)

private fun postDebitContract(save: (Transaction) -> UUID) = "$BASE_URL/debit" meta {
    operationId = "$BASE_URL/debit"
    summary = "Post a debit transaction"
    tags += "$BASE_URL/debit".asTag()
    receiving(
        creditDebitLens to CreditDebit(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        )
    )
    returning(CREATED to UUID.randomUUID().toString())
} bindContract POST to postCreditDebitHandler(DEBIT, save)

private fun multipleDebitContract(save: (List<Transaction>) -> List<UUID>) = "$MULTIPLE_URL/debit" meta {
    operationId = "$MULTIPLE_URL/debit"
    summary = "Post multiple debit transactions"
    tags += multipleTag
    receiving(
        creditDebitListLens to listOf(
            CreditDebit(
                date = Date(LocalDate.of(2020, 1, 1)),
                Category("String"),
                Value(BigDecimal.ZERO),
                Description("String"),
                Quantity(1)
            )
        )
    )
    returning(
        CREATED,
        transactionConfirmationLens to TransactionConfirmation(
            1,
            10f,
            listOf(UUID.randomUUID())
        )
    )
} bindContract POST to postCreditDebitListHandler(DEBIT, save)

private fun postBankTransferContract(save: (Transaction) -> UUID) = "$BASE_URL/bank-transfer" meta {
    operationId = "$BASE_URL/bank-transfer"
    summary = "Post a bank transfer transaction"
    tags += "$BASE_URL/bank-transfer".asTag()
    receiving(
        bankTransferLens to BankTransfer(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1),
            Recipient("String")
        )
    )
    returning(CREATED to UUID.randomUUID().toString())
} bindContract POST to postBankTransferHandler(save)

private fun multipleBankTransferContract(save: (List<Transaction>) -> List<UUID>) =
    "$MULTIPLE_URL/bank-transfer" meta {
        operationId = "$MULTIPLE_URL/bank-transfer"
        summary = "Post multiple bank transfer transactions"
        tags += multipleTag
        receiving(
            bankTransferListLens to listOf(
                BankTransfer(
                    date = Date(LocalDate.of(2020, 1, 1)),
                    Category("String"),
                    Value(BigDecimal.ZERO),
                    Description("String"),
                    Quantity(1),
                    Recipient("String")
                )
            )
        )
        returning(
            CREATED,
            transactionConfirmationLens to TransactionConfirmation(
                1,
                10f,
                listOf(UUID.randomUUID())
            )
        )
    } bindContract POST to postBankTransferListHandler(save)

private fun personalTransferRouteContract(save: (Transaction) -> UUID) =
    "$BASE_URL/personal-transfer" meta {
        operationId = "$BASE_URL/personal-transfer"
        summary = "Post a personal transfer transaction"
        tags += "$BASE_URL/personal-transfer".asTag()
        receiving(
            personalTransferLens to PersonalTransfer(
                date = Date(LocalDate.of(2020, 1, 1)),
                Category("String"),
                Value(BigDecimal.ZERO),
                Description("String"),
                Outbound("String"),
                Inbound("String")
            )
        )
        returning(CREATED to UUID.randomUUID().toString())
    } bindContract POST to postPersonalTransferHandler(save)

private fun multiplePersonalTransferContract(save: (List<Transaction>) -> List<UUID>) =
    "$MULTIPLE_URL/personal-transfer" meta {
        operationId = "$MULTIPLE_URL/personal-transfer"
        summary = "Post multiple personal transfer transactions"
        tags += multipleTag
        receiving(
            personalTransferListLens to listOf(
                PersonalTransfer(
                    date = Date(LocalDate.of(2020, 1, 1)),
                    Category("String"),
                    Value(BigDecimal.ZERO),
                    Description("String"),
                    Outbound("String"),
                    Inbound("String")
                )
            )
        )
        returning(
            CREATED,
            transactionConfirmationLens to TransactionConfirmation(
                1,
                10f,
                listOf(UUID.randomUUID())
            )
        )
    } bindContract POST to postPersonalTransferListHandler(save)

private fun postIncomeContract(save: (Transaction) -> UUID) = "$BASE_URL/income" meta {
    operationId = "$BASE_URL/income"
    summary = "Post an income transaction"
    tags += "$BASE_URL/income".asTag()
    receiving(
        incomeLens to Income(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Source("String")
        )
    )
} bindContract POST to postIncomeHandler(save)

private fun multipleIncomeContract(save: (List<Transaction>) -> List<UUID>) = "$MULTIPLE_URL/income" meta {
    operationId = "$MULTIPLE_URL/income"
    summary = "Post multiple income transactions"
    tags += multipleTag
    receiving(
        incomeListLens to listOf(
            Income(
                date = Date(LocalDate.of(2020, 1, 1)),
                Category("String"),
                Value(BigDecimal.ZERO),
                Description("String"),
                Source("String")
            )
        )
    )
    returning(
        CREATED,
        transactionConfirmationLens to TransactionConfirmation(
            1,
            10f,
            listOf(UUID.randomUUID())
        )
    )
    returning(CREATED to UUID.randomUUID().toString())
} bindContract POST to postIncomeListHandler(save)

private fun putCreditContract(save: (Entity<Transaction>) -> Unit) = "$BASE_URL/credit" meta {
    operationId = "$BASE_URL/credit/put"
    summary = "Update a credit transaction"
    tags += "$BASE_URL/credit".asTag()
    receiving(
        entityCreditDebitLens to CreditDebit(
            Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        ).asRandomEntity()
    )
    returning(NO_CONTENT)
} bindContract Method.PUT to putCreditDebitTransactionHandler(CREDIT, save)

private fun putDebitContract(save: (Entity<Transaction>) -> Unit) = "$BASE_URL/debit" meta {
    operationId = "$BASE_URL/debit/put"
    summary = "Update a debit transaction"
    tags += "$BASE_URL/debit".asTag()
    receiving(
        entityCreditDebitLens to CreditDebit(
            Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        ).asRandomEntity()
    )
    returning(NO_CONTENT)
} bindContract Method.PUT to putCreditDebitTransactionHandler(DEBIT, save)

private fun putBankTransferContract(save: (Entity<Transaction>) -> Unit) = "$BASE_URL/bank-transfer" meta {
    operationId = "$BASE_URL/bank-transfer/put"
    summary = "Update a bank transfer transaction"
    tags += "$BASE_URL/bank-transfer".asTag()
    receiving(
        entityBankTransferLens to BankTransfer(
            Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1),
            Recipient("String")
        ).asRandomEntity()
    )
    returning(NO_CONTENT)
} bindContract Method.PUT to putBankTransferTransactionHandler(save)

private fun putPersonalTransferContract(save: (Entity<Transaction>) -> Unit) = "$BASE_URL/personal-transfer" meta {
    operationId = "$BASE_URL/personal-transfer/put"
    summary = "Update a personal transfer standing order"
    tags += "$BASE_URL/personal-transfer".asTag()
    receiving(
        entityPersonalTransferLens to PersonalTransfer(
            Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Outbound("String"),
            Inbound("String")
        ).asRandomEntity()
    )
    returning(NO_CONTENT)
} bindContract Method.PUT to putPersonalTransferTransactionHandler(save)

private fun putIncomeContract(save: (Entity<Transaction>) -> Unit) = "$BASE_URL/income" meta {
    operationId = "$BASE_URL/income/put"
    summary = "Update an income transaction"
    tags += "$BASE_URL/income".asTag()
    receiving(
        entityIncomeLens to Income(
            Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Source("String")
        ).asRandomEntity()
    )
    returning(NO_CONTENT)
} bindContract Method.PUT to putIncomeTransactionHandler(save)

private fun deleteContract(delete: (UUID) -> Unit) = BASE_URL meta {
    operationId = "$BASE_URL/delete"
    summary = "Delete a transaction"
    tags += tag
    queries += idQuery
    returning(NO_CONTENT)
} bindContract Method.DELETE to deleteEntityHandler(delete)
