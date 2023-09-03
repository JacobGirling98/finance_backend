package support

import config.CustomJackson
import config.properties
import dao.csv.DescriptionMappingCsvDatabase
import dao.csv.StandingOrderCsvDatabase
import dao.csv.TransactionCsvDatabase
import domain.Category
import domain.Date
import domain.Description
import domain.DescriptionMapping
import domain.FrequencyQuantity
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.StandingOrder
import domain.Transaction
import domain.Value
import domain.frequencyFrom
import domain.transactionTypeFrom
import java.io.File
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration

fun txtToCsv(txtFile: String, csvFile: String) {
    val lines = File(txtFile).readLines()

    val headers = "id,value"
    val body = lines.joinToString("\n") { "${UUID.randomUUID()},$it" }

    File(csvFile).writeText("$headers\n$body")
}

fun standingOrders() {
    val lines = File("${properties.dataLocation}/standing_orders.csv").readLines()
    val standingOrders = lines.drop(1).map { line ->
        line.split(",").let {
            StandingOrder(
                Date(LocalDate.parse(it[0])),
                FrequencyQuantity(1),
                frequencyFrom(it[1]),
                Category(it[11]),
                Value.of(it[4].toDouble()),
                Description(it[10]),
                transactionTypeFrom(it[5]),
                Outgoing(it[3].toBoolean()),
                Quantity(it[12].toInt()),
                if (it[8].isEmpty()) null else Recipient(it[8]),
                if (it[7].isEmpty()) null else Inbound(it[7]),
                if (it[6].isEmpty()) null else Outbound(it[6])
            )
        }
    }

    val targetFile = "${properties.dataLocation}/standing_orders_new.csv"
    File(targetFile).writeText("id,next_date,frequency,category,value,description,type,outgoing,quantity,recipient,inbound,outbound\n")

    val database = StandingOrderCsvDatabase(Duration.ZERO, targetFile)

    database.save(standingOrders)
    database.flush()
}

fun transactions() {
    val lines = File("${properties.dataLocation}/data.csv").readLines()

    val transactions = lines.drop(1).map { line ->
        line.split(",").let {
            Transaction(
                Date(LocalDate.parse(it[0])),
                Category(it[9]),
                Value.of(it[2].toDouble()),
                Description(it[8]),
                transactionTypeFrom(it[3]),
                Outgoing(it[1].toBoolean()),
                Quantity(it[10].toInt()),
                if (it[6].isEmpty()) null else Recipient(it[6]),
                if (it[5].isEmpty()) null else Inbound(it[5]),
                if (it[4].isEmpty()) null else Outbound(it[4]),
                if (it[7].isEmpty()) null else Source(it[7])
            )
        }
    }

    val targetFile = "${properties.dataLocation}/transactions.csv"
    File(targetFile).writeText("id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity\n")

    val database = TransactionCsvDatabase(Duration.ZERO, targetFile)

    database.save(transactions)
    database.flush()
}

fun descriptionMappings() {
    val mappings = File("${properties.dataLocation}/description_mappings.txt").readLines()
        .map { CustomJackson.mapper.readValue(it, DescriptionMapping::class.java) }

    val targetFile = "${properties.dataLocation}/description_mappings.csv"
    File(targetFile).writeText("id,full_description,short_description\n")

    val database = DescriptionMappingCsvDatabase(Duration.ZERO, targetFile)

    database.save(mappings)
    database.flush()
}

fun main() {
    txtToCsv("${properties.dataLocation}/accounts.txt", "${properties.dataLocation}/accounts.csv")
    txtToCsv("${properties.dataLocation}/categories.txt", "${properties.dataLocation}/categories.csv")
    txtToCsv("${properties.dataLocation}/income_source.txt", "${properties.dataLocation}/income_sources.csv")
    txtToCsv("${properties.dataLocation}/logins.txt", "${properties.dataLocation}/logins.csv")
    txtToCsv("${properties.dataLocation}/payees.txt", "${properties.dataLocation}/payees.csv")

    standingOrders()

    transactions()

    descriptionMappings()
}
