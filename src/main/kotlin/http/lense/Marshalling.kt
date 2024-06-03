package http.lense

import config.CustomJackson.auto
import dao.AuditableEntity
import dao.Entity
import dao.Page
import domain.Date
import domain.DateRange
import domain.DescriptionMapping
import domain.Headlines
import domain.Login
import domain.Reminder
import domain.StandingOrder
import domain.Transaction
import http.model.ReminderId
import http.model.Transaction.BankTransfer
import http.model.Transaction.CreditDebit
import http.model.Transaction.Income
import http.model.Transaction.PersonalTransfer
import http.model.Transaction.TransactionConfirmation
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens

inline fun <reified T : Any> biDiBodyLens(): BiDiBodyLens<T> = Body.auto<T>().toLens()

inline fun <reified T : Any> entitiesBiDiBodyLens(): BiDiBodyLens<List<Entity<T>>> = biDiBodyLens<List<Entity<T>>>()

val referenceEntitiesLens = entitiesBiDiBodyLens<String>()

val descriptionsLens = biDiBodyLens<List<DescriptionMapping>>()
val descriptionEntitiesLens = entitiesBiDiBodyLens<DescriptionMapping>()

val creditDebitLens = biDiBodyLens<CreditDebit>()
val creditDebitListLens = biDiBodyLens<List<CreditDebit>>()

val bankTransferLens = biDiBodyLens<BankTransfer>()
val bankTransferListLens = biDiBodyLens<List<BankTransfer>>()

val personalTransferLens = biDiBodyLens<PersonalTransfer>()
val personalTransferListLens = biDiBodyLens<List<PersonalTransfer>>()

val incomeLens = biDiBodyLens<Income>()
val incomeListLens = biDiBodyLens<List<Income>>()

val loginLens = biDiBodyLens<Login>()

val transactionConfirmationLens = biDiBodyLens<TransactionConfirmation>()

val dateRangeListLens = biDiBodyLens<List<DateRange>>()

val transactionEntityListLens = biDiBodyLens<List<Entity<Transaction>>>()

val headlinesLens = biDiBodyLens<Headlines>()

val standingOrderLens = biDiBodyLens<StandingOrder>()
val standingOrderListLens = biDiBodyLens<List<Entity<StandingOrder>>>()

val creditDebitStandingOrderLens = biDiBodyLens<http.model.StandingOrder.CreditDebit>()

val bankTransferStandingOrderLens = biDiBodyLens<http.model.StandingOrder.BankTransfer>()

val personalTransferStandingOrderLens = biDiBodyLens<http.model.StandingOrder.PersonalTransfer>()

val incomeStandingOrderLens = biDiBodyLens<http.model.StandingOrder.Income>()

val entityCreditDebitStandingOrderLens = biDiBodyLens<Entity<http.model.StandingOrder.CreditDebit>>()

val entityBankTransferStandingOrderLens = biDiBodyLens<Entity<http.model.StandingOrder.BankTransfer>>()

val entityPersonalTransferStandingOrderLens = biDiBodyLens<Entity<http.model.StandingOrder.PersonalTransfer>>()

val entityIncomeStandingOrderLens = biDiBodyLens<Entity<http.model.StandingOrder.Income>>()

val dateLens = biDiBodyLens<Date>()

val stringListLens = biDiBodyLens<List<String>>()

val transactionPageLens = biDiBodyLens<Page<AuditableEntity<Transaction>>>()

val entityCreditDebitLens = biDiBodyLens<Entity<CreditDebit>>()

val entityBankTransferLens = biDiBodyLens<Entity<BankTransfer>>()

val entityPersonalTransferLens = biDiBodyLens<Entity<PersonalTransfer>>()

val entityIncomeLens = biDiBodyLens<Entity<Income>>()

val reminderIdLens = biDiBodyLens<ReminderId>()

val reminderLens = biDiBodyLens<Reminder>()

val reminderEntityListLens = biDiBodyLens<List<Entity<Reminder>>>()

val reminderEntityLens = biDiBodyLens<Entity<Reminder>>()
