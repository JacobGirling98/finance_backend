package helpers.fixtures

import config.CustomJackson
import org.http4k.core.Response

inline fun <reified T : Any> Response.deserialize(): T = CustomJackson.autoBody<T>().toLens().extract(this)

