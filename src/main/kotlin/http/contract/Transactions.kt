package http.contract

import dao.Database
import dao.Entity
import dao.Page
import dao.entityOf
import domain.*
import domain.Date
import domain.TransactionType.CREDIT
import domain.TransactionType.DEBIT
import http.asTag
import http.handler.*
import http.lense.*
import http.model.Transaction.BankTransfer
import http.model.Transaction.CreditDebit
import http.model.Transaction.Income
import http.model.Transaction.PersonalTransfer
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status
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
    getPaginatedDataRoute { pageNumber, pageSize -> processor.selectAll(pageNumber, pageSize) },
    getPaginatedSearchDataRoute { term, pageNumber, pageSize -> processor.search(term, pageNumber, pageSize) }
)

private fun getPaginatedDataRoute(
    selectAll: (pageNumber: PageNumber, pageSize: PageSize) -> Page<Entity<Transaction>>
) = BASE_URL meta {
    operationId = BASE_URL
    summary = "Get paginated transactions"
    tags += tag
    queries += pageNumberQuery
    queries += pageSizeQuery
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
                        Source("Nullable String")
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
} bindContract GET to paginatedTransactionsHandler(selectAll)

private fun getPaginatedSearchDataRoute(
    search: (term: String, pageNumber: PageNumber, pageSize: PageSize) -> Page<Entity<Transaction>>
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
                        Source("Nullable String")
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
    tags += tag
    receiving(
        creditDebitLens to CreditDebit(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        )
    )
    returning(Status.NO_CONTENT)
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
    returning(Status.NO_CONTENT)
} bindContract POST to postCreditDebitListHandler(CREDIT, save)

private fun postDebitContract(save: (Transaction) -> UUID) = "$BASE_URL/debit" meta {
    operationId = "$BASE_URL/debit"
    summary = "Post a debit transaction"
    tags += tag
    receiving(
        creditDebitLens to CreditDebit(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        )
    )
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
} bindContract POST to postCreditDebitListHandler(DEBIT, save)

private fun postBankTransferContract(save: (Transaction) -> UUID) = "$BASE_URL/bank-transfer" meta {
    operationId = "$BASE_URL/bank-transfer"
    summary = "Post a bank transfer transaction"
    tags += tag
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
    } bindContract POST to postBankTransferListHandler(save)

private fun personalTransferRouteContract(save: (Transaction) -> UUID) =
    "$BASE_URL/personal-transfer" meta {
        operationId = "$BASE_URL/personal-transfer"
        summary = "Post a personal transfer transaction"
        tags += tag
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
    } bindContract POST to postPersonalTransferListHandler(save)

private fun postIncomeContract(save: (Transaction) -> UUID) = "$BASE_URL/income" meta {
    operationId = "$BASE_URL/income"
    summary = "Post an income transaction"
    tags += tag
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
} bindContract POST to postIncomeListHandler(save)
