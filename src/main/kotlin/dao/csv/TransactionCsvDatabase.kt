package dao.csv

import domain.AddedBy
import domain.Category
import domain.Date
import domain.Description
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.Transaction
import domain.Value
import domain.transactionTypeFrom
import java.time.LocalDateTime
import kotlin.time.Duration

class TransactionCsvDatabase(
    syncPeriod: Duration,
    fileLoc: String,
    now: () -> LocalDateTime = { LocalDateTime.now() }
) : CsvDatabase<Transaction>(syncPeriod, fileLoc, now) {
    override fun headers(): String =
        "date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,added_by"

    override fun domainFromCommaSeparatedList(row: List<String>): Transaction {
        val transaction = try {
            Transaction(
                Date(row[indexOfColumn("date")].toDate()),
                Category(row[indexOfColumn("category")]),
                Value.of(row[indexOfColumn("value")].toDouble()),
                Description(row[indexOfColumn("description")]),
                transactionTypeFrom(row[indexOfColumn("transaction_type")]),
                Outgoing(row[indexOfColumn("outgoing")].toBoolean()),
                Quantity(row[indexOfColumn("quantity")].toInt()),
                row[indexOfColumn("destination")].toValueOrNull { Recipient(it) },
                row[indexOfColumn("inbound_account")].toValueOrNull { Inbound(it) },
                row[indexOfColumn("outbound_account")].toValueOrNull { Outbound(it) },
                row[indexOfColumn("source")].toValueOrNull { Source(it) },
                AddedBy(row[indexOfColumn("added_by")])
            )
        } catch (e: Exception) {
            println("Failed to parse row: $row")
            throw e
        }
        return transaction
    }

    override fun Transaction.toRow(): String =
        "${date.value},${outgoing.value},${value.value},${type.type},${outbound?.value.orEmpty()},${inbound?.value.orEmpty()},${recipient?.value.orEmpty()},${source?.value.orEmpty()},${description.value},${category.value},${quantity.value},${addedBy.value}"
}
