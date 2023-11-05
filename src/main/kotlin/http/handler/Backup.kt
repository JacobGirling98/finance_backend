package http.handler

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status

fun backupHandler(fn: () -> Unit): HttpHandler = {
    fn()
    Response(Status.NO_CONTENT)
}
