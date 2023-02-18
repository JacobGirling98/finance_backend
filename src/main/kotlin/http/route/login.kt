package http.route

import dao.Login
import dao.LoginDatabase
import http.asTag
import http.handler.loginHandler
import http.lense.loginLens
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import java.time.LocalDate

private const val URL = "/last-login"

fun loginContracts(loginDatabase: LoginDatabase) = listOf(
    lastLoginRoute(loginDatabase)
)

private fun lastLoginRoute(loginDatabase: LoginDatabase) = URL meta {
    operationId = URL
    summary = "Get the latest login"
    tags += URL.asTag()
    returning(OK, loginLens to Login(LocalDate.of(2023, 1, 1)))
} bindContract GET to loginHandler { loginDatabase.lastLogin(Login(LocalDate.now())) }