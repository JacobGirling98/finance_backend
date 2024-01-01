package config

import http.google.GoogleDrive
import resource.*
import java.time.LocalDate

val transactionsProcessor = TransactionProcessor(transactionDatabase)

val loginSynchroniser = LoginSynchroniser(loginDatabase)

val standingOrderProcessor = StandingOrderProcessor(standingOrderDatabase, transactionDatabase, LocalDate::now)

val googleDrive = GoogleDrive(properties.google.credentialsFile)

val googleDriveSynchroniser = GoogleDriveSynchroniser(googleDrive)

val reminderProcessor = ReminderProcessor(reminderDatabase, LocalDate::now)