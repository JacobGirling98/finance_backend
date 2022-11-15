package finance.http.filter

import finance.dao.Login
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import java.time.LocalDate

fun lastLoginFilter(save: (date: Login) -> Unit) = Filter { next: HttpHandler ->
    { request: Request ->
        save(Login(LocalDate.now()))
        val response = next(request)
        response
    }
}