package dao

import domain.Category
import java.io.File

class ReferenceData(private val dataDirectory: String) {

    var categories: List<String> = emptyList()
    var accounts: List<String> = emptyList()

    fun initialise() {
        categories = readCategories()
        accounts = readAccounts()
    }

    private fun read(file: String): List<String> = File("$dataDirectory/$file").readLines()

    private fun readCategories(): List<String> = read("categories.txt").sorted()

    private fun readAccounts(): List<String> = read("accounts.txt").sorted()
}