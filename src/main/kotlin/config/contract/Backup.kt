package config.contract

import http.asTag
import http.google.Synchronisable
import http.handler.unitHandlerFor
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.NO_CONTENT
import resource.GoogleDriveSynchroniser

private const val URL = "/backup"

fun googleBackupContracts(databases: List<Synchronisable>, googleDriveSynchroniser: GoogleDriveSynchroniser) = listOf(
    pushToGoogle { googleDriveSynchroniser.pushToDrive(databases) },
    pullFromGoogle { googleDriveSynchroniser.pullFromDrive(databases) }
)

private fun pushToGoogle(push: () -> Unit) = "$URL/push" meta {
    operationId = "$URL/push"
    summary = "Push files to Google Drive"
    tags += URL.asTag()
    returning(NO_CONTENT)
} bindContract POST to unitHandlerFor(push)

private fun pullFromGoogle(pull: () -> Unit) = "$URL/pull" meta {
    operationId = "$URL/pull"
    summary = "Pull files from Google Drive"
    tags += URL.asTag()
    returning(NO_CONTENT)
} bindContract POST to unitHandlerFor(pull)
