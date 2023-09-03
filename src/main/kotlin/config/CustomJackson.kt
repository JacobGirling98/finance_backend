package config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import domain.Category
import domain.Date
import domain.Description
import domain.EndDate
import domain.FrequencyQuantity
import domain.FullDescription
import domain.Inbound
import domain.Login
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.ShortDescription
import domain.Source
import domain.StartDate
import domain.Value
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.bigDecimal
import org.http4k.format.boolean
import org.http4k.format.int
import org.http4k.format.localDate
import org.http4k.format.text
import org.http4k.format.withStandardMappings

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
        .localDate(::Date, Date::value)
        .localDate(::StartDate, StartDate::value)
        .localDate(::EndDate, EndDate::value)
        .localDate(::Login, Login::value)
        .boolean(::Outgoing, Outgoing::value)
        .int(::FrequencyQuantity, FrequencyQuantity::value)
        .done()
        .deactivateDefaultTyping()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
)
