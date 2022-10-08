package dao

import domain.Description
import http.MyJackson
import java.io.File

class ReferenceData(private val dataDirectory: String) {

    var categories: List<String> = emptyList()
    var accounts: List<String> = emptyList()
    var sources: List<String> = emptyList()
    var descriptions: List<Description> = emptyList()
    var payees: List<String> = emptyList()

    fun initialise() {
        categories = readCategories()
        accounts = readAccounts()
        sources = readSources()
        payees = readPayees()
        descriptions = readDescriptions()
    }

    private fun read(file: String): List<String> = File("$dataDirectory/$file").readLines()

    private fun readCategories(): List<String> = read("categories.txt").sorted()

    private fun readAccounts(): List<String> = read("accounts.txt").sorted()

    private fun readSources(): List<String> = read("income_source.txt").sorted()

    private fun readPayees(): List<String> = read("payees.txt").sorted()

    private fun readDescriptions(): List<Description> = MyJackson.mapper.readValue(
        File("$dataDirectory/description_mappings.json").readText(),
        Array<Description>::class.java
    ).toList().sortedBy { it.shortDescription.value }
}