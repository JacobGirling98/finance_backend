package http.handler

import domain.Date
import domain.Login
import http.lense.dateLens
import http.lense.loginLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun loginHandler(lastLogin: () -> Login): HttpHandler = {
    Response(Status.OK).with(loginLens of lastLogin())
}

fun lastTransactionHandler(lastTransaction: () -> Date?): HttpHandler = {
    lastTransaction()?.let {
        Response(Status.OK).with(dateLens of it)
    } ?: Response(Status.BAD_REQUEST).body("Could not find a transaction")
}
