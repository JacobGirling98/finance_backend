package finance.utils

import finance.config.CustomJackson
import finance.config.DATA_LOC
import finance.domain.DescriptionMapping
import java.io.File



fun main() {
    val json = CustomJackson.mapper.readValue(File("$DATA_LOC/description_mappings.json").readText(), Array<DescriptionMapping>::class.java).toList()
//    File("$DATA_LOC/description_mappings.json").delete()
    val file = File("$DATA_LOC/description_mappings.txt")
    json.forEach {
        file.appendText("${CustomJackson.mapper.writeValueAsString(it)}\n")
    }
}