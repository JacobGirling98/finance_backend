package dao.csv

import domain.Category
import domain.Date
import domain.Description
import domain.FrequencyQuantity
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.StandingOrder
import domain.Value
import domain.frequencyFrom
import domain.transactionTypeFrom
import java.time.LocalDateTime
import kotlin.time.Duration

class StandingOrderCsvDatabase(syncPeriod: Duration, file: String, now: () -> LocalDateTime = { LocalDateTime.now() }) :
    CsvDatabase<StandingOrder>(syncPeriod, file, now) {

    override fun headers(): String =
        "next_date,frequency_quantity,frequency_unit,category,value,description,type,outgoing,quantity,recipient,inbound,outbound,source"

    override fun domainFromCommaSeparatedList(row: List<String>): StandingOrder = StandingOrder(
        Date(row[indexOfColumn("next_date")].toDate()),
        FrequencyQuantity(row[indexOfColumn("frequency_quantity")].toInt()),
        frequencyFrom(row[indexOfColumn("frequency_unit")]),
        Category(row[indexOfColumn("category")]),
        Value.of(row[indexOfColumn("value")].toDouble()),
        Description(row[indexOfColumn("description")]),
        transactionTypeFrom(row[indexOfColumn("type")]),
        Outgoing(row[indexOfColumn("outgoing")].toBoolean()),
        Quantity(row[indexOfColumn("quantity")].toInt()),
        row[indexOfColumn("recipient")].toValueOrNull { Recipient(it) },
        row[indexOfColumn("inbound")].toValueOrNull { Inbound(it) },
        row[indexOfColumn("outbound")].toValueOrNull { Outbound(it) },
        row[indexOfColumn("source")].toValueOrNull { Source(it) }
    )

    override fun StandingOrder.toRow(): String =
        "${date.value},${frequencyQuantity.value},${frequency.value},${category.value},${value.value},${description.value},${type.type},${outgoing.value},${quantity.value},${recipient?.value.orEmpty()},${inbound?.value.orEmpty()},${outbound?.value.orEmpty()},${source?.value.orEmpty()}"
}
