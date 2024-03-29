package dao.csv

import domain.*
import kotlin.time.Duration

class TransactionCsvDatabase(syncPeriod: Duration, fileLoc: String) : CsvDatabase<Transaction>(syncPeriod, fileLoc) {
    override fun headers(): String =
        "date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,added_by"

    override fun domainFromCommaSeparatedList(row: List<String>): Transaction = Transaction(
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

    override fun Transaction.toRow(): String =
        "${date.value},${outgoing.value},${value.value},${type.type},${outbound?.value.orEmpty()},${inbound?.value.orEmpty()},${recipient?.value.orEmpty()},${source?.value.orEmpty()},${description.value},${category.value},${quantity.value},${addedBy.value}"
}
