package config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

data class Properties(
    val dataLocation: String
)

fun readProperties(profile: String): Properties {
    val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
    val targetFile = if (profile == "docker") File("/app/docker.yaml") else
        File("src/main/resources/properties").listFiles()?.first { it.nameWithoutExtension == profile }
            ?: error("File not found")
    return yamlMapper.readValue(targetFile, Properties::class.java)
}
