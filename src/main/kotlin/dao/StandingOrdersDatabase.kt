package dao

import domain.*
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate

open class StandingOrdersDatabase(dataDirectory: String) : CsvDatabase<StandingOrder, StandingOrderColumns>() {

    override var data = mutableListOf<StandingOrder>()
    override val file = File("$dataDirectory/standing_orders.csv")

    override fun read() {
        read { data, columns ->
            StandingOrder(
                Date(LocalDate.parse(data[columns.nextDateColumn])),
                frequencyFrom(data[columns.frequencyColumn]),
                Category(data[columns.categoryColumn]),
                Value(BigDecimal(data[columns.valueColumn])),
                Description(data[columns.descriptionColumn]),
                transactionTypeFrom(data[columns.transactionTypeColumn]),
                Outgoing(data[columns.outgoingColumn].toBoolean()),
                quantity = Quantity(data[columns.quantityColumn].toInt()),
                recipient = data[columns.recipientColumn].valueOrNull()?.let { Recipient(it) },
                outbound = data[columns.outboundColumn].valueOrNull()?.let { Outbound(it) },
                inbound = data[columns.inboundColumn].valueOrNull()?.let { Inbound(it) },
                source = data[columns.sourceColumn].valueOrNull()?.let { Source(it) }
            )
        }
    }

    override fun columnIndicesFrom(columns: List<String>) = StandingOrderColumns(
        columns.indexOf("next_date"),
        columns.indexOf("frequency"),
        columns.indexOf("category"),
        columns.indexOf("value"),
        columns.indexOf("quantity"),
        columns.indexOf("description"),
        columns.indexOf("transaction_type"),
        columns.indexOf("destination"),
        columns.indexOf("inbound_account"),
        columns.indexOf("outbound_account"),
        columns.indexOf("source"),
        columns.indexOf("outgoing")
    )

    override fun File.writeHeaders() {
        writeText("next_date,frequency,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity\n")
    }

    override fun update(id: Int, data: StandingOrder) {

    }

    override fun save(data: StandingOrder) {
        file.writeLine("${data.nextDate.value},${data.frequency.value},,${data.outgoing.asString()},${data.value.value},${data.type.type},${data.outbound?.value.valueOrBlank()},${data.inbound?.value.valueOrBlank()},${data.recipient?.value.valueOrBlank()},${data.source?.value.valueOrBlank()},${data.description.value.valueOrNull()},${data.category.value},${data.quantity.value}")
    }
}
