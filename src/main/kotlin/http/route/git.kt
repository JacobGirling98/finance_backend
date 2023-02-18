package http.route

import http.asTag
import http.git.GitClient
import http.handler.gitSyncHandler
import org.http4k.contract.meta
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.NO_CONTENT

private const val BASE_URL = "/git/sync"

fun gitContracts(gitClient: GitClient) = listOf(syncRoute(gitClient))

private fun syncRoute(gitClient: GitClient) = BASE_URL meta {
    operationId = BASE_URL
    summary = "Sync data with git"
    tags += BASE_URL.asTag()
    returning(NO_CONTENT)
} bindContract POST to gitSyncHandler { gitClient.sync() }
