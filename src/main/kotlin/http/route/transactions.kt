package http.route

import dao.Database
import domain.*
import domain.TransactionType.CREDIT
import domain.TransactionType.DEBIT
import http.asTag
import http.handler.*
import http.lense.*
import http.model.BankTransfer
import http.model.CreditDebit
import http.model.Income
import http.model.PersonalTransfer
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Status
import java.math.BigDecimal
import java.time.LocalDate

private const val BASE_URL = "/transaction"
private const val MULTIPLE_URL = "$BASE_URL/multiple"
private val tag = BASE_URL.asTag()
private val multipleTag = MULTIPLE_URL.asTag()

fun transactionContracts(database: Database<Transaction>) = listOf(
    creditRoute(database),
    multipleCreditRoute(database),
    debitRoute(database),
    multipleDebitRoute(database),
    bankTransferRoute(database),
    multipleBankTransferRoute(database),
    personalTransferRoute(database),
    multiplePersonalTransferRoute(database),
    incomeRoute(database),
    multipleIncomeRoute(database)
)

private fun creditRoute(database: Database<Transaction>) = "$BASE_URL/credit" meta {
    operationId = "$BASE_URL/credit"
    summary = "Post a credit transaction"
    tags += tag
    receiving(
        creditDebitLens to CreditDebit(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1),
        )
    )
    returning(Status.NO_CONTENT)
} bindContract POST to postCreditDebitHandler(CREDIT) { database.save(it) }

private fun multipleCreditRoute(database: Database<Transaction>) = "$MULTIPLE_URL/credit" meta {
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
                Quantity(1),
            )
        )
    )
    returning(Status.NO_CONTENT)
} bindContract POST to postCreditDebitListHandler(CREDIT) { database.save(it) }

private fun debitRoute(database: Database<Transaction>) = "$BASE_URL/debit" meta {
    operationId = "$BASE_URL/debit"
    summary = "Post a debit transaction"
    tags += tag
    receiving(
        creditDebitLens to CreditDebit(
            date = Date(LocalDate.of(2020, 1, 1)),
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1),
        )
    )
} bindContract POST to postCreditDebitHandler(DEBIT) { database.save(it) }

private fun multipleDebitRoute(database: Database<Transaction>) = "$MULTIPLE_URL/debit" meta {
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
                Quantity(1),
            )
        )
    )
} bindContract POST to postCreditDebitListHandler(DEBIT) { database.save(it) }

private fun bankTransferRoute(database: Database<Transaction>) = "$BASE_URL/bank-transfer" meta {
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
} bindContract POST to postBankTransferHandler { database.save(it) }

private fun multipleBankTransferRoute(database: Database<Transaction>) = "$MULTIPLE_URL/bank-transfer" meta {
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
} bindContract POST to postBankTransferListHandler { database.save(it) }

private fun personalTransferRoute(database: Database<Transaction>) = "$BASE_URL/personal-transfer" meta {
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
} bindContract POST to postPersonalTransferHandler { database.save(it) }

private fun multiplePersonalTransferRoute(database: Database<Transaction>) = "$MULTIPLE_URL/personal-transfer" meta {
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
} bindContract POST to postPersonalTransferListHandler { database.save(it) }

private fun incomeRoute(database: Database<Transaction>) = "$BASE_URL/income" meta {
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
} bindContract POST to postIncomeHandler { database.save(it) }

private fun multipleIncomeRoute(database: Database<Transaction>) = "$MULTIPLE_URL/income" meta {
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
} bindContract POST to postIncomeListHandler { database.save(it) }