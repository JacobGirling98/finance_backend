package http.contract

import http.asTag
import http.google.Synchronisable
import http.handler.backupHandler
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.NO_CONTENT
import resource.GoogleDriveSynchroniser

private const val URL = "/backup"

fun googleBackupContracts(databases: List<Synchronisable>, googleDriveSynchroniser: GoogleDriveSynchroniser) = listOf(
    pushToGoogle(databases) { googleDriveSynchroniser.pushToDrive(it) },
    pullFromGoogle(databases) { googleDriveSynchroniser.pullFromDrive(it) }
)

private fun pushToGoogle(databases: List<Synchronisable>, push: (Synchronisable) -> Unit) = "$URL/push" meta {
    operationId = "$URL/push"
    summary = "Push files to Google Drive"
    tags += URL.asTag()
    returning(NO_CONTENT)
} bindContract POST to backupHandler { databases.forEach { push(it) } }

private fun pullFromGoogle(databases: List<Synchronisable>, pull: (Synchronisable) -> Unit) = "$URL/pull" meta {
    operationId = "$URL/pull"
    summary = "Pull files from Google Drive"
    tags += URL.asTag()
    returning(NO_CONTENT)
} bindContract POST to backupHandler { databases.forEach { pull(it) } }