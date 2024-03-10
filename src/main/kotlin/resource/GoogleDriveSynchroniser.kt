package resource

import http.google.GoogleDrive
import http.google.Synchronisable
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GoogleDriveSynchroniser(private val googleDrive: GoogleDrive) {

    fun pushToDrive(database: Synchronisable) {
        googleDrive.updateFile(database)
    }

    fun pullFromDrive(database: Synchronisable) {
        database.overwrite(googleDrive.readText(database.latestFile().name))
    }

    fun pushToDrive(databases: Collection<Synchronisable>) {
        databases.forEachParallel { pushToDrive(it) }
    }

    fun pullFromDrive(databases: Collection<Synchronisable>) {
        databases.forEachParallel { pullFromDrive(it) }
    }

    private fun <T> Collection<T>.forEachParallel(fn: (T) -> Unit) = runBlocking {
        this@forEachParallel.forEach { launch { fn(it) } }
    }
}
