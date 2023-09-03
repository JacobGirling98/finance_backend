package unit.fixtures

import config.CustomJackson
import org.http4k.core.Body

inline fun <reified T> Body.toObject() = CustomJackson.mapper.readValue(this.toString(), T::class.java)
