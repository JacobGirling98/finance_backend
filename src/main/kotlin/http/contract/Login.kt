package http.contract

import domain.Login
import http.asTag
import http.handler.loginHandler
import http.lense.biDiBodyLens
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import java.time.LocalDate

private const val URL = "/last-login"

fun loginContracts(lastLogin: () -> LocalDate?) = listOf(
    lastLoginRoute(lastLogin)
)

private fun lastLoginRoute(lastLogin: () -> LocalDate?) = URL meta {
    operationId = URL
    summary = "Get the latest login"
    tags += URL.asTag()
    returning(OK, biDiBodyLens<LocalDate>() to LocalDate.of(2023, 1, 1))
} bindContract GET to loginHandler { Login(lastLogin()!!) }
