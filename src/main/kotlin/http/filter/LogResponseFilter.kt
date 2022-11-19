package http.filter

import config.logger
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request

fun logResponseFilter() = Filter { next: HttpHandler ->
    { request: Request ->
        val response = next(request)
        logger.info { "${request.method} - ${request.uri} - ${response.status}" }
        response
    }
}