package http

import org.http4k.contract.Tag

fun String.asTag() = Tag(this)