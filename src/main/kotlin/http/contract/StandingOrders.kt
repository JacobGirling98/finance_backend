package http.contract

import dao.Database
import dao.Entity
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
import http.handler.getStandingOrdersHandler
import http.handler.postBankTransferStandingOrderHandler
import http.handler.postCreditDebitStandingOrderHandler
import http.handler.postIncomeStandingOrderHandler
import http.handler.postPersonalTransferStandingOrderHandler
import http.lense.bankTransferStandingOrderLens
import http.lense.creditDebitStandingOrderLens
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
private val tag = BASE_URL.asTag()

fun standingOrdersContract(repository: Database<StandingOrder, UUID>) = listOf(
    getStandingOrdersContract { repository.selectAll() },
    creditContract { repository.save(it) },
    debitContract { repository.save(it) },
    bankTransferContract { repository.save(it) },
    personalTransferContract { repository.save(it) },
    incomeContract { repository.save(it) }
)

private fun creditContract(save: (StandingOrder) -> UUID) = "$BASE_URL/credit" meta {
    operationId = "$BASE_URL/credit"
    summary = "Post a credit standing order"
    tags += tag
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

private fun debitContract(save: (StandingOrder) -> UUID) = "$BASE_URL/debit" meta {
    operationId = "$BASE_URL/debit"
    summary = "Post a debit transaction"
    tags += tag
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

private fun bankTransferContract(save: (StandingOrder) -> UUID) = "$BASE_URL/bank-transfer" meta {
    operationId = "$BASE_URL/bank-transfer"
    summary = "Post a bank transfer standing order"
    tags += tag
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

private fun personalTransferContract(save: (StandingOrder) -> UUID) = "$BASE_URL/personal-transfer" meta {
    operationId = "$BASE_URL/personal-transfer"
    summary = "Post a personal transfer standing order"
    tags += tag
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

private fun incomeContract(save: (StandingOrder) -> UUID) = "$BASE_URL/income" meta {
    operationId = "$BASE_URL/income"
    summary = "Post an income standing order"
    tags += tag
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
                    Outbound("String")
                )
            )
        )
    )
} bindContract Method.GET to getStandingOrdersHandler(standingOrders)
