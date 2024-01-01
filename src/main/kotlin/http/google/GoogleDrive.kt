package http.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import config.AppMode
import config.logger
import config.properties
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

class GoogleDrive(private val credentialsFile: String) {

    private val service = run {
        try {
            val credentials =
                GoogleCredentials.fromStream(FileInputStream(credentialsFile)).createScoped(setOf(DriveScopes.DRIVE))
            Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                HttpCredentialsAdapter(credentials)
            ).setApplicationName("finance-app").build()
        } catch (e: Exception) {
            logger.error { "Could not initialise Google Drive service: ${e.message}" }
            throw RuntimeException("Could not initialise Google Drive service")
        }
    }

    private val folderIds = mapOf(
        AppMode.DEV to "17t5C6CFBmXXk79wPKOQOoCsCbL7yKk9R",
        AppMode.TEST to "17t5C6CFBmXXk79wPKOQOoCsCbL7yKk9R",
        AppMode.PROD to "17GWICqqUNRDRDWN_RzVO3hdN6Tjrt1jR"
    )

    private val targetDirectoryId by lazy { folderIds[properties.appMode]!! }

    private val idMappings = mutableMapOf<String, String>()

    fun readText(fileName: String): String {
        val id = getCacheableId(fileName)

        val outputStream = ByteArrayOutputStream()

        service.files().get(id).executeMediaAndDownloadTo(outputStream)

        return outputStream.toString()
    }

    fun updateFile(synchronisable: Synchronisable) {
        val latestFile = synchronisable.latestFile()

        val id = getCacheableId(latestFile.name)

        val fileContent = FileContent(synchronisable.mimeType.value, synchronisable.latestFile())

        service.files().update(id, File(), fileContent).execute()
    }

    private fun getCacheableId(fileName: String): String = idMappings[fileName] ?: run {
        val newId = getFileIdOrThrow(fileName)
        idMappings[fileName] = newId
        newId
    }

    private fun getFileIdOrThrow(fileName: String): String {
        val file = service.files().list()
            .setQ("name = '$fileName' and '$targetDirectoryId' in parents")
            .execute()
            .files

        if (file.size > 1) {
            throw RuntimeException("More than one file found with $fileName")
        }

        return file.first().id
    }
}
