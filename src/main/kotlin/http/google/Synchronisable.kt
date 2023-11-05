package http.google

import java.io.File

interface Synchronisable {

    val mimeType: MimeType

    fun latestFile(): File

    fun overwrite(data: String)
}
