package dao

import domain.*
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate


class Database(dataDirectory: String) {

    var data = mutableListOf<Transaction>()

    private val file = File("$dataDirectory/data.csv")

    fun read() {
        val lines = file.readLines()
        val columns = columnIndicesFrom(lines[0].split(","))
        data = lines
            .subList(1, lines.size).map { row ->
                row.split(",").let { data ->
                    Transaction(
                        Date(LocalDate.parse(data[columns.dateColumn])),
                        Category(data[columns.categoryColumn]),
                        Value(BigDecimal(data[columns.valueColumn])),
                        Description(data[columns.descriptionColumn]),
                        transactionTypeFrom(data[columns.transactionTypeColumn]),
                        data[columns.outgoingColumn].toBoolean(),
                        quantity = Quantity(data[columns.quantityColumn].toInt()),
                        recipient = data[columns.recipientColumn].valueOrNull()?.let { Recipient(it) },
                        outbound = data[columns.outboundColumn].valueOrNull()?.let { Outbound(it) },
                        inbound = data[columns.inboundColumn].valueOrNull()?.let { Inbound(it) },
                        source = data[columns.sourceColumn].valueOrNull()?.let { Source(it) }
                    )
                }
            }.toMutableList()
    }

    fun flush() {
        file.writeHeaders()
        save(data)
    }

    fun save(transaction: Transaction) {
        file.appendText("${transaction.date.value},${transaction.outgoing.asString()},${transaction.value.value},${transaction.type.type},${transaction.outbound?.value.valueOrBlank()},${transaction.inbound?.value.valueOrBlank()},${transaction.recipient?.value.valueOrBlank()},${transaction.source?.value.valueOrBlank()},${transaction.description.value.valueOrNull()},${transaction.category.value},${transaction.quantity.value}\n")
    }

    fun save(transactions: List<Transaction>) {
        transactions.forEach { save(it) }
    }

}

private fun File.writeHeaders() {
    writeText("date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity\n")
}

private data class Columns(
    val dateColumn: Int,
    val categoryColumn: Int,
    val valueColumn: Int,
    val quantityColumn: Int,
    val descriptionColumn: Int,
    val transactionTypeColumn: Int,
    val recipientColumn: Int,
    val inboundColumn: Int,
    val outboundColumn: Int,
    val sourceColumn: Int,
    val outgoingColumn: Int
)

private fun columnIndicesFrom(columns: List<String>) = Columns(
    columns.indexOf("date"),
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

private fun String.valueOrNull(): String? = this.ifEmpty { null }
private fun String?.valueOrBlank(): String = this ?: ""
private fun Boolean.asString(): String = when (this) {
    true -> "True"
    false -> "False"
}