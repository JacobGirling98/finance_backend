package finance.http.handler

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status

fun gitSyncHandler(sync: () -> Unit): HttpHandler = {
    sync()
    Response(Status.OK)
}