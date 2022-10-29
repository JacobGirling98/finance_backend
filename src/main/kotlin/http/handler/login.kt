package http.handler

import dao.Login
import http.lense.loginLens
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

fun loginHandler(lastLogin: () -> Login): HttpHandler = {
    Response(Status.OK).with(loginLens of lastLogin())
}