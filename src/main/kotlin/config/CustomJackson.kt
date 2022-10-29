package config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dao.Login
import domain.*
import org.http4k.format.*

object CustomJackson : ConfigurableJackson(
    KotlinModule.Builder().build()
        .asConfigurable()
        .withStandardMappings()
        .text(::FullDescription, FullDescription::value)
        .text(::ShortDescription, ShortDescription::value)
        .text(::Category, Category::value)
        .bigDecimal(::Value, Value::value)
        .text(::Description, Description::value)
        .int(::Quantity, Quantity::value)
        .text(::Recipient, Recipient::value)
        .text(::Inbound, Inbound::value)
        .text(::Outbound, Outbound::value)
        .text(::Source, Source::value)
        .localDate(::Login, Login::value)
        .localDate(::Date, Date::value)
        .done()
        .deactivateDefaultTyping()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
)
