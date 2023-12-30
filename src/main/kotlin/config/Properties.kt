package config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File


data class SyncProperties(
    val sync: Long
)

data class CsvProperties(
    val descriptionMapping: SyncProperties,
    val login: SyncProperties,
    val standingOrder: SyncProperties,
    val transaction: SyncProperties,
    val account: SyncProperties,
    val category: SyncProperties,
    val incomeSource: SyncProperties,
    val payee: SyncProperties
)

data class Google(
    val credentialsFile: String
)

data class Properties(
    val dataLocation: String,
    val csv: CsvProperties,
    val appMode: AppMode,
    val google: Google
)

fun readProperties(profile: String): Properties {
    val yamlMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
    val targetFile = if (profile == "docker") {
        File("/app/docker.yaml")
    } else {
        File("src/main/resources/properties").listFiles()?.first { it.nameWithoutExtension == profile }
            ?: error("File not found")
    }
    return yamlMapper.readValue(targetFile, Properties::class.java)
}
