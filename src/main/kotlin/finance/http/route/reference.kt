package finance.http.route

import finance.dao.ReferenceData
import finance.http.handler.descriptionsHandler
import finance.http.handler.postDescriptionsHandler
import finance.http.handler.referenceHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes

fun referenceRoutes(referenceData: ReferenceData) = routes(
    "/reference" bind routes(
        "/categories" bind GET to referenceHandler { referenceData.categories },
        "/accounts" bind GET to referenceHandler { referenceData.accounts },
        "/sources" bind GET to referenceHandler { referenceData.sources },
        "/payees" bind GET to referenceHandler { referenceData.payees },
        "/descriptions" bind routes(
            "" bind GET to descriptionsHandler { referenceData.descriptions },
            "/multiple" bind POST to postDescriptionsHandler { referenceData.save(it) }
        )
    )
)

