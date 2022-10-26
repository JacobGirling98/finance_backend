package utils

import config.CustomJackson
import config.DATA_LOC
import dao.ReferenceData
import java.io.File

private val referenceData = ReferenceData(DATA_LOC)

fun main() {
    referenceData.initialise()
    File("$DATA_LOC/description_mappings.json").delete()
    val file = File("$DATA_LOC/description_mappings.txt")
    referenceData.descriptions.forEach {
        file.appendText("${CustomJackson.mapper.writeValueAsString(it)}\n")
    }
}