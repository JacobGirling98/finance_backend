package finance.http.lense

import finance.config.CustomJackson.auto
import finance.dao.Login
import finance.domain.DescriptionMapping
import finance.http.model.BankTransfer
import finance.http.model.CreditDebit
import finance.http.model.Income
import finance.http.model.PersonalTransfer
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens


val referenceLens: BiDiBodyLens<List<String>> = Body.auto<List<String>>().toLens()

val descriptionsLens: BiDiBodyLens<List<DescriptionMapping>> = Body.auto<List<DescriptionMapping>>().toLens()

val creditDebitLens: BiDiBodyLens<CreditDebit> = Body.auto<CreditDebit>().toLens()

val bankTransferLens: BiDiBodyLens<BankTransfer> = Body.auto<BankTransfer>().toLens()

val personalTransferLens: BiDiBodyLens<PersonalTransfer> = Body.auto<PersonalTransfer>().toLens()

val incomeLens: BiDiBodyLens<Income> = Body.auto<Income>().toLens()

val creditDebitListLens: BiDiBodyLens<List<CreditDebit>> = Body.auto<List<CreditDebit>>().toLens()

val bankTransferListLens: BiDiBodyLens<List<BankTransfer>> = Body.auto<List<BankTransfer>>().toLens()

val personalTransferListLens: BiDiBodyLens<List<PersonalTransfer>> = Body.auto<List<PersonalTransfer>>().toLens()

val incomeListLens: BiDiBodyLens<List<Income>> = Body.auto<List<Income>>().toLens()

val loginLens: BiDiBodyLens<Login> = Body.auto<Login>().toLens()