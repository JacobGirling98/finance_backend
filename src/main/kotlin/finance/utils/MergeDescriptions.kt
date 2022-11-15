package finance.utils

import finance.config.DATA_LOC
import finance.dao.TransactionsDatabase
import finance.dao.ReferenceData
import finance.domain.DescriptionMapping
import finance.domain.FullDescription
import finance.domain.ShortDescription

private val referenceData = ReferenceData(DATA_LOC)
private val database = TransactionsDatabase(DATA_LOC)

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