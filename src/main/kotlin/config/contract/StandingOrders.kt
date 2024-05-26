package config.contract

import dao.Database
import dao.Entity
import dao.asRandomEntity
import dao.entityOf
import domain.Category
import domain.Date
import domain.Description
import domain.Frequency
import domain.FrequencyQuantity
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.StandingOrder
import domain.TransactionType
import domain.Value
import http.asTag
import http.handler.deleteEntityHandler
import http.handler.getStandingOrdersHandler
import http.handler.postBankTransferStandingOrderHandler
import http.handler.postCreditDebitStandingOrderHandler
import http.handler.postIncomeStandingOrderHandler
import http.handler.postPersonalTransferStandingOrderHandler
import http.handler.putBankTransferStandingOrderHandler
import http.handler.putCreditDebitStandingOrderHandler
import http.handler.putIncomeStandingOrderHandler
import http.handler.putPersonalTransferStandingOrderHandler
import http.lense.bankTransferStandingOrderLens
import http.lense.creditDebitStandingOrderLens
import http.lense.entityBankTransferStandingOrderLens
import http.lense.entityCreditDebitStandingOrderLens
import http.lense.entityIncomeStandingOrderLens
import http.lense.entityPersonalTransferStandingOrderLens
import http.lense.idQuery
import http.lense.incomeStandingOrderLens
import http.lense.personalTransferStandingOrderLens
import http.lense.standingOrderListLens
import http.model.StandingOrder.BankTransfer
import http.model.StandingOrder.CreditDebit
import http.model.StandingOrder.Income
import http.model.StandingOrder.PersonalTransfer
import org.http4k.contract.meta
import org.http4k.core.Method
import org.http4k.core.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

private const val BASE_URL = "/standing-orders"

fun standingOrdersContracts(repository: Database<StandingOrder, UUID>) = listOf(
    getStandingOrdersContract { repository.selectAll() },
    postCreditContract { repository.save(it) },
    postDebitContract { repository.save(it) },
    postBankTransferContract { repository.save(it) },
    postPersonalTransferContract { repository.save(it) },
    postIncomeContract { repository.save(it) },
    putCreditContract { repository.update(it) },
    putDebitContract { repository.update(it) },
    putBankTransferContract { repository.update(it) },
    putPersonalTransferContract { repository.update(it) },
    putIncomeContract { repository.update(it) },
    deleteContract { repository.delete(it) }
)

private fun postCreditContract(save: (StandingOrder) -> UUID) = "$BASE_URL/credit" meta {
    operationId = "$BASE_URL/credit/post"
    summary = "Add a credit standing order"
    tags += "$BASE_URL/credit".asTag()
    receiving(
        creditDebitStandingOrderLens to CreditDebit(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        )
    )
    returning(Status.NO_CONTENT)
} bindContract Method.POST to postCreditDebitStandingOrderHandler(TransactionType.CREDIT, save)

private fun putCreditContract(save: (Entity<StandingOrder>) -> Unit) = "$BASE_URL/credit" meta {
    operationId = "$BASE_URL/credit/put"
    summary = "Update a credit standing order"
    tags += "$BASE_URL/credit".asTag()
    receiving(
        entityCreditDebitStandingOrderLens to CreditDebit(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        ).asRandomEntity()
    )
    returning(Status.NO_CONTENT)
} bindContract Method.PUT to putCreditDebitStandingOrderHandler(TransactionType.CREDIT, save)

private fun postDebitContract(save: (StandingOrder) -> UUID) = "$BASE_URL/debit" meta {
    operationId = "$BASE_URL/debit/post"
    summary = "Add a debit transaction"
    tags += "$BASE_URL/debit".asTag()
    receiving(
        creditDebitStandingOrderLens to CreditDebit(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        )
    )
    returning(Status.NO_CONTENT)
} bindContract Method.POST to postCreditDebitStandingOrderHandler(TransactionType.DEBIT, save)

private fun putDebitContract(save: (Entity<StandingOrder>) -> Unit) = "$BASE_URL/debit" meta {
    operationId = "$BASE_URL/debit/put"
    summary = "Update a debit standing order"
    tags += "$BASE_URL/debit".asTag()
    receiving(
        entityCreditDebitStandingOrderLens to CreditDebit(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1)
        ).asRandomEntity()
    )
    returning(Status.NO_CONTENT)
} bindContract Method.PUT to putCreditDebitStandingOrderHandler(TransactionType.DEBIT, save)

private fun postBankTransferContract(save: (StandingOrder) -> UUID) = "$BASE_URL/bank-transfer" meta {
    operationId = "$BASE_URL/bank-transfer/post"
    summary = "Add a bank transfer standing order"
    tags += "$BASE_URL/bank-transfer".asTag()
    receiving(
        bankTransferStandingOrderLens to BankTransfer(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1),
            Recipient("String")
        )
    )
    returning(Status.NO_CONTENT)
} bindContract Method.POST to postBankTransferStandingOrderHandler(save)

private fun putBankTransferContract(save: (Entity<StandingOrder>) -> Unit) = "$BASE_URL/bank-transfer" meta {
    operationId = "$BASE_URL/bank-transfer/put"
    summary = "Update a bank transfer standing order"
    tags += "$BASE_URL/bank-transfer".asTag()
    receiving(
        entityBankTransferStandingOrderLens to BankTransfer(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Quantity(1),
            Recipient("String")
        ).asRandomEntity()
    )
    returning(Status.NO_CONTENT)
} bindContract Method.PUT to putBankTransferStandingOrderHandler(save)

private fun postPersonalTransferContract(save: (StandingOrder) -> UUID) = "$BASE_URL/personal-transfer" meta {
    operationId = "$BASE_URL/personal-transfer/post"
    summary = "Add a personal transfer standing order"
    tags += "$BASE_URL/personal-transfer".asTag()
    receiving(
        personalTransferStandingOrderLens to PersonalTransfer(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Outbound("String"),
            Inbound("String")
        )
    )
    returning(Status.NO_CONTENT)
} bindContract Method.POST to postPersonalTransferStandingOrderHandler(save)

private fun putPersonalTransferContract(save: (Entity<StandingOrder>) -> Unit) = "$BASE_URL/personal-transfer" meta {
    operationId = "$BASE_URL/personal-transfer/put"
    summary = "Update a personal transfer standing order"
    tags += "$BASE_URL/personal-transfer".asTag()
    receiving(
        entityPersonalTransferStandingOrderLens to PersonalTransfer(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Outbound("String"),
            Inbound("String")
        ).asRandomEntity()
    )
    returning(Status.NO_CONTENT)
} bindContract Method.PUT to putPersonalTransferStandingOrderHandler(save)

private fun postIncomeContract(save: (StandingOrder) -> UUID) = "$BASE_URL/income" meta {
    operationId = "$BASE_URL/income/post"
    summary = "Add an income standing order"
    tags += "$BASE_URL/income".asTag()
    receiving(
        incomeStandingOrderLens to Income(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Source("String")
        )
    )
    returning(Status.NO_CONTENT)
} bindContract Method.POST to postIncomeStandingOrderHandler(save)

private fun putIncomeContract(save: (Entity<StandingOrder>) -> Unit) = "$BASE_URL/income" meta {
    operationId = "$BASE_URL/income/put"
    summary = "Update an income standing order"
    tags += "$BASE_URL/income".asTag()
    receiving(
        entityIncomeStandingOrderLens to Income(
            Date(LocalDate.of(2020, 1, 1)),
            FrequencyQuantity(1),
            Frequency.MONTHLY,
            Category("String"),
            Value(BigDecimal.ZERO),
            Description("String"),
            Source("String")
        ).asRandomEntity()
    )
    returning(Status.NO_CONTENT)
} bindContract Method.PUT to putIncomeStandingOrderHandler(save)

private fun getStandingOrdersContract(standingOrders: () -> List<Entity<StandingOrder>>) = BASE_URL meta {
    operationId = "Get all standing orders"
    summary = "Interact with standing orders"
    tags += BASE_URL.asTag()
    returning(
        Status.OK,
        standingOrderListLens to listOf(
            entityOf(
                StandingOrder(
                    Date(LocalDate.of(2023, 1, 1)),
                    FrequencyQuantity(1),
                    Frequency.MONTHLY,
                    Category("String"),
                    Value(BigDecimal.ZERO),
                    Description("String"),
                    TransactionType.CREDIT,
                    Outgoing(false),
                    Quantity(1),
                    Recipient("String"),
                    Inbound("String"),
                    Outbound("String"),
                    Source("String")
                )
            )
        )
    )
} bindContract Method.GET to getStandingOrdersHandler(standingOrders)

private fun deleteContract(delete: (UUID) -> Unit) = BASE_URL meta {
    operationId = "$BASE_URL/delete"
    summary = "Delete a standing order"
    tags += BASE_URL.asTag()
    queries += idQuery
    returning(Status.NO_CONTENT)
} bindContract Method.DELETE to deleteEntityHandler(delete)
