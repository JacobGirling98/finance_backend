package http.lense

import config.CustomJackson.auto
import dao.Login
import domain.DateRange
import domain.DescriptionMapping
import domain.Transaction
import http.model.*
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.Query
import org.http4k.lens.QueryLens

inline fun <reified T : Any> biDiBodyLens(): BiDiBodyLens<T> = Body.auto<T>().toLens()

val referenceLens = biDiBodyLens<List<String>>()

val descriptionsLens = biDiBodyLens<List<DescriptionMapping>>()

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

val transactionListLens = biDiBodyLens<List<Transaction>>()