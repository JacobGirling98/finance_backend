package config

import dao.csv.DescriptionMappingCsvDatabase
import dao.csv.LoginCsvDatabase
import dao.csv.ReminderCsvDatabase
import dao.csv.StandingOrderCsvDatabase
import dao.csv.StringCsvDatabase
import dao.csv.TransactionCsvDatabase
import http.google.Synchronisable
import kotlin.time.Duration.Companion.milliseconds

val accountDatabase =
    StringCsvDatabase(properties.csv.account.sync.milliseconds, "${properties.dataLocation}/accounts.csv")

val categoryDatabase =
    StringCsvDatabase(properties.csv.category.sync.milliseconds, "${properties.dataLocation}/categories.csv")

val incomeSourceDatabase =
    StringCsvDatabase(properties.csv.incomeSource.sync.milliseconds, "${properties.dataLocation}/income_sources.csv")

val payeeDatabase = StringCsvDatabase(properties.csv.payee.sync.milliseconds, "${properties.dataLocation}/payees.csv")

val descriptionMappingDatabase =
    DescriptionMappingCsvDatabase(
        properties.csv.descriptionMapping.sync.milliseconds,
        "${properties.dataLocation}/description_mappings.csv"
    )

val transactionDatabase =
    TransactionCsvDatabase(properties.csv.transaction.sync.milliseconds, "${properties.dataLocation}/transactions.csv")

val standingOrderDatabase = StandingOrderCsvDatabase(
    properties.csv.standingOrder.sync.milliseconds,
    "${properties.dataLocation}/standing_orders.csv"
)

val loginDatabase = LoginCsvDatabase(properties.csv.login.sync.milliseconds, "${properties.dataLocation}/logins.csv")

val reminderDatabase =
    ReminderCsvDatabase(properties.csv.reminder.sync.milliseconds, "${properties.dataLocation}/reminders.csv")

val synchronisableDatabases: List<Synchronisable> = listOf(
    descriptionMappingDatabase,
    transactionDatabase,
    standingOrderDatabase,
    loginDatabase,
    accountDatabase,
    categoryDatabase,
    payeeDatabase,
    incomeSourceDatabase,
    reminderDatabase
)
