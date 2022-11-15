package finance.http.route

import finance.http.git.GitClient
import finance.http.handler.gitSyncHandler
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

fun gitRoutes(gitClient: GitClient) = routes(
    "/git/sync" bind POST to gitSyncHandler { gitClient.sync() }
)