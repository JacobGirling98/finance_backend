package script

import config.properties
import dao.csv.*
import http.google.GoogleDrive
import http.google.Synchronisable
import resource.GoogleDriveSynchroniser
import kotlin.time.Duration

private val accountDatabase =
    StringCsvDatabase(Duration.ZERO, "${properties.dataLocation}/accounts.csv", lazyDataLoad = true)

private val categoryDatabase =
    StringCsvDatabase(Duration.ZERO, "${properties.dataLocation}/categories.csv", lazyDataLoad = true)

private val incomeSourceDatabase =
    StringCsvDatabase(Duration.ZERO, "${properties.dataLocation}/income_sources.csv", lazyDataLoad = true)

private val payeeDatabase = StringCsvDatabase(
    Duration.ZERO,
    "${properties.dataLocation}/payees.csv",
    lazyDataLoad = true
)

private val descriptionMappingDatabase =
    DescriptionMappingCsvDatabase(
        Duration.ZERO,
        "${properties.dataLocation}/description_mappings.csv"
    , lazyDataLoad = true
    )

private val transactionDatabase =
    TransactionCsvDatabase(Duration.ZERO, "${properties.dataLocation}/transactions.csv", lazyDataLoad = true)

private val standingOrderDatabase = StandingOrderCsvDatabase(
    Duration.ZERO,
    "${properties.dataLocation}/standing_orders.csv"
, lazyDataLoad = true
)

private val loginDatabase = LoginCsvDatabase(Duration.ZERO, "${properties.dataLocation}/logins.csv", lazyDataLoad = true)

private val reminderDatabase =
    ReminderCsvDatabase(Duration.ZERO, "${properties.dataLocation}/reminders.csv", lazyDataLoad = true)

private val budgetDatabase =
    BudgetCsvDatabase(Duration.ZERO, "${properties.dataLocation}/budgets.csv", lazyDataLoad = true)

private val synchronisableDatabases: List<Synchronisable> = listOf(
    descriptionMappingDatabase,
    transactionDatabase,
    standingOrderDatabase,
    loginDatabase,
    accountDatabase,
    categoryDatabase,
    payeeDatabase,
    incomeSourceDatabase,
    reminderDatabase,
    budgetDatabase
)

private val googleDrive = GoogleDrive(properties.google.credentialsFile)

private val googleDriveSynchroniser = GoogleDriveSynchroniser(googleDrive)

fun main() {
    googleDriveSynchroniser.pullFromDrive(synchronisableDatabases)
}