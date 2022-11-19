package utils

import config.CustomJackson
import config.properties
import domain.DescriptionMapping
import java.io.File


fun main() {
    val json = CustomJackson.mapper.readValue(
        File("${properties.dataLocation}/description_mappings.json").readText(),
        Array<DescriptionMapping>::class.java
    ).toList()
//    File("$DATA_LOC/description_mappings.json").delete()
    val file = File("${properties.dataLocation}/description_mappings.txt")
    json.forEach {
        file.appendText("${CustomJackson.mapper.writeValueAsString(it)}\n")
    }
}