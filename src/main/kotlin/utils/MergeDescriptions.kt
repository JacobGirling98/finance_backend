package utils

import dao.TransactionsDatabase
import dao.ReferenceData
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription

private val referenceData = ReferenceData("")
private val database = TransactionsDatabase("")

fun main() {
    referenceData.initialise()
    database.read()

    val shortDescriptions = referenceData.descriptions.map { it.shortDescription.value }
    val dataDescriptions = database.data.map { it.description.value }

    val newDescriptions = dataDescriptions.filterNot { shortDescriptions.contains(it) }.toSet().toList().map {
        DescriptionMapping(FullDescription(it), ShortDescription(it))
    }

    referenceData.save(newDescriptions)
}