package http.route

import dao.Login
import dao.LoginDatabase
import http.handler.loginHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.LocalDate

fun loginRoutes(loginDatabase: LoginDatabase) = routes(
    "/last-login" bind GET to loginHandler { loginDatabase.lastLogin(Login(LocalDate.now())) }
)