package http.route

import dao.LoginDatabase
import http.handler.loginHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes

fun loginRoutes(loginDatabase: LoginDatabase) = routes(
    "/last-login" bind GET to loginHandler { loginDatabase.lastLogin() }
)