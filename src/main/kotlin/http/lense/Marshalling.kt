package http.lense

import config.CustomJackson.auto
import dao.Entity
import domain.DateRange
import domain.DescriptionMapping
import domain.Headlines
import domain.Login
import domain.StandingOrder
import domain.Transaction
import http.model.BankTransfer
import http.model.CreditDebit
import http.model.Income
import http.model.PersonalTransfer
import http.model.TransactionConfirmation
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
