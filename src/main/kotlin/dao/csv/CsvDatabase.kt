package dao.csv

import dao.AuditableEntity
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

abstract class CsvDatabase<Domain : Comparable<Domain>>(
    private val syncPeriod: Duration,
    fileName: String,
    lazyDataLoad: Boolean = false,
    now: () -> LocalDateTime = { LocalDateTime.now() }
) : InMemoryDatabase<Domain>(now = now), Synchronisable {

    private var flushIsScheduled = false
    private lateinit var dataListenerTask: TimerTask

    private val file = File(fileName)

    private lateinit var columns: List<String>

    init {
        if (!lazyDataLoad) {
            loadDataFromFile()
        }
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
        val lines = data.split("\n")
        loadData(lines)
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
        loadData(file.readLines())
    }

    private fun loadData(linesWithHeader: List<String>) {
        if (linesWithHeader.isEmpty()) return
        setColumns(linesWithHeader.first())
        setData(linesWithHeader.drop(1))
    }

    private fun setColumns(headersString: String) {
        columns = headersString.split(",")
    }

    private fun setData(fileLines: List<String>) {
        this.data = fileLines
            .map { readRow(it) }
            .associateBy { it.id }
            .toMutableMap()
    }

    private fun readRow(row: String): AuditableEntity<Domain> = row.split(",").let {
        AuditableEntity(UUID.fromString(it[0]), domainFromCommaSeparatedList(it), LocalDateTime.parse(it[1]))
    }

    private fun scheduleFileSync() {
        if (syncPeriod.isPositive()) {
            Timer().schedule(delay = syncPeriod.inWholeMilliseconds, period = syncPeriod.inWholeMilliseconds) {
                flush()
            }
        }
    }
}
