package http.marshaller

import config.CustomJackson
import http.models.BankTransfer
import http.models.CreditDebit
import http.models.Income
import http.models.PersonalTransfer

fun creditDebitMarshaller(value: String): CreditDebit = CustomJackson.mapper.readValue(value, CreditDebit::class.java)

fun bankTransferMarshaller(value: String): BankTransfer = CustomJackson.mapper.readValue(value, BankTransfer::class.java)

fun personalTransferMarshaller(value: String): PersonalTransfer = CustomJackson.mapper.readValue(value, PersonalTransfer::class.java)

fun incomeMarshaller(value: String): Income = CustomJackson.mapper.readValue(value, Income::class.java)