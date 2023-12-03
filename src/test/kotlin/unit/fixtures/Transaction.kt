package unit.fixtures

import dao.Entity
import dao.Page
import domain.Category
import domain.Date
import domain.Description
import domain.HasNextPage
import domain.HasPreviousPage
import domain.Outgoing
import domain.PageNumber
import domain.PageSize
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
import java.util.*

val uuid: UUID = UUID.randomUUID()

fun anEntity(id: UUID = uuid, transaction: () -> Transaction) = Entity(id, transaction())

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
    null
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
    null
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
    Source("Work")
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
    null
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
    null
)

fun aPage(transaction: Transaction) = Page(
    listOf(
        anEntity { transaction }
    ),
    PageNumber(1),
    PageSize(5),
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
