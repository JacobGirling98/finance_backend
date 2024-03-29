package helpers.fixtures

import dao.Entity
import dao.Page
import domain.AddedBy
import domain.Category
import domain.Date
import domain.Description
import domain.HasNextPage
import domain.HasPreviousPage
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Recipient
import domain.Source
import domain.TotalElements
import domain.TotalPages
import domain.Transaction
import domain.TransactionType.BANK_TRANSFER
import domain.TransactionType.CREDIT
import domain.TransactionType.DEBIT
import domain.TransactionType.INCOME
import domain.TransactionType.PERSONAL_TRANSFER
import domain.Value
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

val uuid: UUID = UUID.randomUUID()

fun anEntity(id: UUID = uuid, transaction: Transaction, now: () -> LocalDateTime = { LocalDateTime.now() }) = Entity(id, transaction, now())

fun aDebitTransaction() = Transaction(
    date,
    category,
    value,
    description,
    DEBIT,
    Outgoing(true),
    quantity,
    null,
    null,
    null,
    null,
    addedBy
)

fun aCreditTransaction() = Transaction(
    date,
    category,
    value,
    description,
    CREDIT,
    Outgoing(true),
    quantity,
    null,
    null,
    null,
    null,
    addedBy
)

fun aWagesIncome() = Transaction(
    date,
    Category("Wages"),
    value,
    description,
    INCOME,
    Outgoing(false),
    quantity,
    null,
    null,
    null,
    Source("Work"),
    addedBy
)

fun aPersonalTransferTransaction() = Transaction(
    date,
    category,
    value,
    description,
    PERSONAL_TRANSFER,
    Outgoing(false),
    quantity,
    null,
    inbound,
    outbound,
    null,
    addedBy
)

fun aBankTransferTransaction() = Transaction(
    date,
    category,
    value,
    description,
    BANK_TRANSFER,
    Outgoing(true),
    quantity,
    recipient,
    null,
    null,
    null,
    addedBy
)

fun aPage(transaction: Transaction, now: () -> LocalDateTime = { LocalDateTime.now() }) = Page(
    listOf(
        anEntity(transaction = transaction, now = now)
    ),
    pageNumber,
    pageSize,
    TotalElements(20),
    TotalPages(4),
    HasPreviousPage(false),
    HasNextPage(true)
)

fun Transaction.withAValueOf(value: Double) = copy(value = Value(BigDecimal.valueOf(value)))

fun Transaction.withADateOf(year: Int = 2020, month: Int = 1, day: Int = 1) =
    copy(date = Date(LocalDate.of(year, month, day)))

fun Transaction.withADateOf(date: LocalDate) =
    copy(date = Date(date))

fun Transaction.withADescriptionOf(value: String) = copy(description = Description(value))

fun Transaction.withACategoryOf(value: String) = copy(category = Category(value))

fun Transaction.withARecipientOf(value: String) = copy(recipient = Recipient(value))

fun Transaction.withAnInboundAccountOf(value: String) = copy(inbound = Inbound(value))

fun Transaction.withAnOutboundAccountOf(value: String) = copy(outbound = Outbound(value))

fun Transaction.withAnIncomeSourceOf(value: String) = copy(source = Source(value))

fun Transaction.addedBy(value: String) = copy(addedBy = AddedBy(value))
