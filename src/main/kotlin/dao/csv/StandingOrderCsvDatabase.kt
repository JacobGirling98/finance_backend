package dao.csv

import domain.*
import kotlin.time.Duration

class StandingOrderCsvDatabase(syncPeriod: Duration, file: String) : CsvDatabase<StandingOrder>(syncPeriod, file) {

    override fun headers(): String =
        "next_date,frequency_quantity,frequency_unit,category,value,description,type,outgoing,quantity,recipient,inbound,outbound"

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
        row[indexOfColumn("outbound")].toValueOrNull { Outbound(it) }
    )

    override fun StandingOrder.toRow(): String =
        "${nextDate.value},${frequencyQuantity.value},${frequencyUnit.value},${category.value},${value.value},${description.value},${type.type},${outgoing.value},${quantity.value},${recipient?.value.orEmpty()},${inbound?.value.orEmpty()},${outbound?.value.orEmpty()}"
}