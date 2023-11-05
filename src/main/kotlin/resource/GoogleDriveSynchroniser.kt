package resource

import http.google.GoogleDrive
import http.google.Synchronisable

class GoogleDriveSynchroniser(private val googleDrive: GoogleDrive) {

    fun pushToDrive(database: Synchronisable) {
        googleDrive.updateFile(database)
    }

    fun pullFromDrive(database: Synchronisable) {
        database.overwrite(googleDrive.readText(database.latestFile().name))
    }
}
