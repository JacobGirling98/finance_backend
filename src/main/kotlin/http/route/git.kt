package http.route

import http.git.GitClient
import http.handler.gitSyncHandler
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

fun gitRoutes(gitClient: GitClient) = routes(
    "/git/sync" bind POST to gitSyncHandler { gitClient.sync() }
)