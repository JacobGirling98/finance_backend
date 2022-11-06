package dao

import domain.Outgoing
import java.io.File

abstract class CsvDatabase<T, U : ColumnIndices> : Database<T> {
    abstract var data: MutableList<T>
    protected abstract val file: File

    abstract fun read()
    protected abstract fun columnIndicesFrom(columns: List<String>): U
    protected abstract fun File.writeHeaders()
    abstract fun update(id: Int, data: T)

    open fun flush() {
        file.writeHeaders()
        save(data)
    }

    protected fun read(createData: (data: List<String>, columns: U) -> T) {
        val lines = file.readLines()
        val columns = columnIndicesFrom(lines[0].split(","))
        data = lines
            .subList(1, lines.size).map { createData(it.split(","), columns) }.toMutableList()
    }

    protected fun String.valueOrNull(): String? = this.ifEmpty { null }

    protected fun String?.valueOrBlank(): String = this ?: ""

    protected fun Outgoing.asString(): String = when (this.value) {
        true -> "True"
        false -> "False"
    }
}