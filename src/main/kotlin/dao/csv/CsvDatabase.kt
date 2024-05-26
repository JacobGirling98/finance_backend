package dao.csv

import dao.Entity
import dao.memory.InMemoryDatabase
import http.google.MimeType
import http.google.Synchronisable
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class CsvDatabase<Domain : Comparable<Domain>>(
    private val syncPeriod: Duration,
    fileName: String,
    now: () -> LocalDateTime = { LocalDateTime.now() },
    private val dataListenerPeriod: Duration = 60.seconds
) : InMemoryDatabase<Domain>(now = now), Synchronisable {

    private var flushIsScheduled = false
    private lateinit var dataListenerTask: TimerTask

    private val file = File(fileName)

    private lateinit var columns: List<String>

    init {
        loadDataFromFile()
        scheduleFileSync()
    }

    protected abstract fun headers(): String

    protected abstract fun Domain.toRow(): String

    protected abstract fun domainFromCommaSeparatedList(row: List<String>): Domain

    override fun latestFile(): File {
        flush()
        return file
    }

    override fun overwrite(data: String) {
        this.data = data.split("\n").drop(1).map { readRow(it) }.associateBy { it.id }.toMutableMap()
        flush()
    }

    override val mimeType: MimeType = MimeType.TEXT_CSV

    fun indexOfColumn(column: String) =
        columns.indexOf(column)

    fun flush() {
        val headers = "id,last_modified,${headers()}"
        val body =
            selectAll().joinToString("\n") { "${it.id},${it.lastModified.format(ISO_LOCAL_DATE_TIME)},${it.domain.toRow()}" }
        file.writeText("$headers\n$body")
    }

    fun String.toDate(): LocalDate = LocalDate.parse(this)

    fun <T> String.toValueOrNull(fn: (String) -> T): T? = if (isBlank()) null else fn(this)

    private fun loadDataFromFile() {
        val lines = file.readLines()
        if (lines.isEmpty()) {
            return
        }
        columns = lines.first().split(",")
        this.data = lines
            .drop(1)
            .map { readRow(it) }
            .associateBy { it.id }
            .toMutableMap()
    }

    private fun readRow(row: String): Entity<Domain> = row.split(",").let {
        Entity(UUID.fromString(it[0]), domainFromCommaSeparatedList(it), LocalDateTime.parse(it[1]))
    }

    private fun scheduleFileSync() {
        if (syncPeriod.isPositive()) {
            Timer().schedule(delay = syncPeriod.inWholeMilliseconds, period = syncPeriod.inWholeMilliseconds) {
                flush()
            }
        }
    }
}
