package config

import http.google.GoogleDrive
import resource.BudgetCalculator
import resource.GoogleDriveSynchroniser
import resource.LoginSynchroniser
import resource.ReminderProcessor
import resource.StandingOrderProcessor
import resource.TransactionProcessor
import java.time.LocalDate

val transactionsProcessor = TransactionProcessor(transactionDatabase)

val loginSynchroniser = LoginSynchroniser(loginDatabase)

val standingOrderProcessor = StandingOrderProcessor(standingOrderDatabase, transactionDatabase, LocalDate::now)

val googleDrive = GoogleDrive(properties.google.credentialsFile)

val googleDriveSynchroniser = GoogleDriveSynchroniser(googleDrive)

val reminderProcessor = ReminderProcessor(reminderDatabase, LocalDate::now)

val budgetCalculator = BudgetCalculator(transactionsProcessor, budgetDatabase)
