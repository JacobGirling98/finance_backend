package http.contract

import dao.Database
import dao.Entity
import dao.entityOf
import domain.Category
import domain.Date
import domain.Description
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.Transaction
import domain.TransactionType.CREDIT
import domain.TransactionType.DEBIT
import domain.Value
import http.asTag
import http.handler.postBankTransferHandler
import http.handler.postBankTransferListHandler
import http.handler.postCreditDebitHandler
import http.handler.postCreditDebitListHandler
import http.handler.postIncomeHandler
import http.handler.postIncomeListHandler
import http.handler.postPersonalTransferHandler
import http.handler.postPersonalTransferListHandler
import http.handler.transactionsHandler
import http.lense.bankTransferLens
import http.lense.bankTransferListLens
import http.lense.creditDebitLens
import http.lense.creditDebitListLens
import http.lense.endDateQuery
import http.lense.incomeLens
import http.lense.incomeListLens
import http.lense.personalTransferLens
import http.lense.personalTransferListLens
import http.lense.startDateQuery
import http.lense.transactionEntityListLens
import http.model.Transaction.BankTransfer
import http.model.Transaction.CreditDebit
import http.model.Transaction.Income
import http.model.Transaction.PersonalTransfer
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

private const val BASE_URL = "/transaction"
private const val MULTIPLE_URL = "$BASE_URL/multiple"
private val tag = BASE_URL.asTag()
private val multipleTag = MULTIPLE_URL.asTag()

fun transactionContracts(database: Database<Transaction, UUID>) = listOf(
    creditContract { database.save(it) },
    multipleCreditContract { database.save(it) },
    debitContract { database.save(it) },
    multipleDebitContract { database.save(it) },
    bankTransferContract { database.save(it) },
    multipleBankTransferContract { database.save(it) },
    personalTransferRouteContract { database.save(it) },
    multiplePersonalTransferContract { database.save(it) },
    incomeContract { database.save(it) },
    multipleIncomeContract { database.save(it) },
    getDataRoute { database.selectAll() }
)

private fun getDataRoute(data: () -> List<Entity<Transaction>>) = BASE_URL meta {
    operationId = BASE_URL
    summary = "Get transactions between two dates"
    tags += tag
    queries += startDateQuery
    queries += endDateQuery
    returning(
        OK,
        transactionEntityListLens to listOf(
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
        )
    )
} bindContract GET to transactionsHandler { data() }

private fun creditContract(save: (Transaction) -> UUID) = "$BASE_URL/credit" meta {
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

private fun debitContract(save: (Transaction) -> UUID) = "$BASE_URL/debit" meta {
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

private fun bankTransferContract(save: (Transaction) -> UUID) = "$BASE_URL/bank-transfer" meta {
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

private fun incomeContract(save: (Transaction) -> UUID) = "$BASE_URL/income" meta {
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
