package config

import http.google.GoogleDrive
import resource.GoogleDriveSynchroniser
import resource.LoginSynchroniser
import resource.StandingOrderProcessor
import resource.TransactionProcessor
import java.time.LocalDate

val transactionsProcessor = TransactionProcessor(transactionDatabase)

val loginSynchroniser = LoginSynchroniser(loginDatabase)

val standingOrderProcessor = StandingOrderProcessor(standingOrderDatabase, transactionDatabase, LocalDate::now)

val googleDrive = GoogleDrive(properties.google.credentialsFile)

val googleDriveSynchroniser = GoogleDriveSynchroniser(googleDrive)