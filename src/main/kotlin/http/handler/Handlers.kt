package http.handler

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.with
import org.http4k.lens.BiDiBodyLens

fun unitHandlerFor(f: () -> Unit): HttpHandler = {
    f()
    Response(NO_CONTENT)
}

fun <T, R> handlerFor(
    f: (T) -> R,
    status: Status,
    requestExtractor: (Request) -> T,
    lens: BiDiBodyLens<R>
): HttpHandler = { request -> Response(status).with(lens of f(requestExtractor(request))) }
