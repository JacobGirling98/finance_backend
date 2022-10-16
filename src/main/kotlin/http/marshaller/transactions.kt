package http.marshaller

import config.CustomJackson
import http.models.Credit

fun creditMarshaller(value: String): Credit = CustomJackson.mapper.readValue(value, Credit::class.java)