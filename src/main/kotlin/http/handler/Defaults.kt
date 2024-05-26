package http.handler

import domain.AddedBy
import org.http4k.core.Request

fun Request.userHeader(): AddedBy = header("user")?.let { AddedBy(it) } ?: AddedBy("finance-app")
