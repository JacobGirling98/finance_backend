package config.contract

import domain.Date
import domain.Login
import http.asTag
import http.handler.lastTransactionHandler
import http.handler.loginHandler
import http.lense.biDiBodyLens
import http.lense.dateLens
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import java.time.LocalDate

private const val LOGIN_URL = "/last-login"
private const val TRANSACTION_URL = "/last-transaction"

fun loginContracts(lastLogin: () -> LocalDate?) = listOf(
    lastLoginRoute(lastLogin)
)

fun lastTransactionContracts(lastUserTransaction: () -> Date?) = listOf(
    lastTransactionRoute(lastUserTransaction)
)

private fun lastLoginRoute(lastLogin: () -> LocalDate?) = LOGIN_URL meta {
    operationId = LOGIN_URL
    summary = "Get the latest login"
    tags += LOGIN_URL.asTag()
    returning(OK, biDiBodyLens<LocalDate>() to LocalDate.of(2023, 1, 1))
} bindContract GET to loginHandler { Login(lastLogin()!!) }

private fun lastTransactionRoute(lastUserTransaction: () -> Date?) = TRANSACTION_URL meta {
    operationId = TRANSACTION_URL
    summary = "Get the latest transaction"
    tags += TRANSACTION_URL.asTag()
    returning(OK, dateLens to Date(LocalDate.of(2023, 1, 1)))
} bindContract GET to lastTransactionHandler { lastUserTransaction() }
