package dao

import domain.Category
import java.io.File

class ReferenceData(private val dataDirectory: String) {

    var categories: List<Category> = emptyList()

    fun initialise() {
        categories = readCategories()
    }

    private fun read(file: String): List<String> = File("$dataDirectory/$file").readLines()

    private fun readCategories(): List<Category> = read("categories.txt").sorted().map { Category(it) }
}