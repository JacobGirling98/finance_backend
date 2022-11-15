package dao

import config.CustomJackson
import domain.DescriptionMapping
import java.io.File

class ReferenceData(private val dataDirectory: String) : Database<DescriptionMapping> {

    var categories: List<String> = emptyList()
    var accounts: List<String> = emptyList()
    var sources: List<String> = emptyList()
    var descriptions: MutableList<DescriptionMapping> = mutableListOf()
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

    fun readDescriptions(): MutableList<DescriptionMapping> = read("description_mappings.txt").map {
        CustomJackson.mapper.readValue(it, DescriptionMapping::class.java)
    }.sortedBy { it.shortDescription.value }.toMutableList()

    override fun save(data: DescriptionMapping) {
        descriptions.add(data)
        File("$dataDirectory/description_mappings.txt").writeLine(
            CustomJackson.mapper.writeValueAsString(data)
        )
    }

    override fun save(data: List<DescriptionMapping>) {
        data.forEach { save(it) }
    }
}