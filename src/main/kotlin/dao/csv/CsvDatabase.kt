package dao.csv

import dao.Entity
import dao.memory.InMemoryDatabase
import java.io.File
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.Duration

abstract class CsvDatabase<Domain>(
    private val syncPeriod: Duration,
    fileLoc: String
) : InMemoryDatabase<Domain>() {

    private val file = File(fileLoc)
    private lateinit var columns: List<String>

    init {
        loadDataFromFile()
        scheduleFileSync()
    }

    protected abstract fun headers(): String

    protected abstract fun Domain.toRow(): String
    protected abstract fun domainFromCommaSeparatedList(row: List<String>): Domain

    fun indexOfColumn(column: String) =
        columns.indexOf(column)

    fun flush() {
        val headers = "id,${headers()}"
        val body = selectAll().joinToString("\n") { "${it.id},${it.domain.toRow()}" }
        file.writeText("$headers\n$body")
    }

    fun String.toDate(): LocalDate = LocalDate.parse(this)

    fun <T> String.toValueOrNull(fn: (String) -> T): T? = if (isBlank()) null else fn(this)

    private fun loadDataFromFile() {
        val lines = file.readLines()
        columns = lines.first().split(",")
        super.data = lines
            .drop(1)
            .map { readRow(it) }
            .associate { it.id to it.domain }
            .toMutableMap()
    }

    private fun readRow(row: String): Entity<Domain> = row.split(",").let {
        Entity(UUID.fromString(it[0]), domainFromCommaSeparatedList(it))
    }

    private fun scheduleFileSync() {
        if (syncPeriod.isPositive()) {
            Timer().schedule(delay = syncPeriod.inWholeMilliseconds, period = syncPeriod.inWholeMilliseconds) {
                flush()
            }
        }
    }
}