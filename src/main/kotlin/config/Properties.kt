package config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

data class Properties(
    val dataLocation: String
)

fun readProperties(profile: String): Properties {
    val resourcesLocation = if (profile != "docker") "src/main/" else ""
    val targetFile =
        File("${resourcesLocation}resources/properties").listFiles()?.first { it.nameWithoutExtension == profile }
            ?: error("Can't find properties files")
    val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
    return yamlMapper.readValue(targetFile, Properties::class.java)
}
