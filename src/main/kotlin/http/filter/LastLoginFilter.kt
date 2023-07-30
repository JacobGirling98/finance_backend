package http.filter

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import java.time.LocalDate

fun lastLoginFilter(save: (date: LocalDate) -> Unit) = Filter { next: HttpHandler ->
    { request: Request ->
        save(LocalDate.now())
        val response = next(request)
        response
    }
}