package http.route

import dao.ReferenceData
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import http.asTag
import http.handler.descriptionsHandler
import http.handler.postDescriptionsHandler
import http.handler.referenceHandler
import http.lense.descriptionsLens
import http.lense.referenceLens
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK

private const val BASE_URL = "reference"
private val tag = BASE_URL.asTag()
private val descriptionsTag = "$BASE_URL/descriptions".asTag()


fun contractReferenceRoutes(referenceData: ReferenceData) = listOf(
    categoriesRoute(referenceData),
    accountsRoute(referenceData),
    sourcesRoute(referenceData),
    payees(referenceData),
    descriptionsRoute(referenceData),
    multipleDescriptionsRoute(referenceData)
)

private fun categoriesRoute(referenceData: ReferenceData) = "$BASE_URL/categories" meta {
    operationId = "$BASE_URL/categories"
    summary = "Get a list of transaction categories"
    tags += tag
    returning(OK, referenceLens to listOf("String"))
} bindContract GET to referenceHandler { referenceData.categories }

private fun accountsRoute(referenceData: ReferenceData) = "$BASE_URL/accounts" meta {
    operationId = "$BASE_URL/categories"
    summary = "Get a list of accounts"
    tags += tag
    returning(OK, referenceLens to listOf("String"))
} bindContract GET to referenceHandler { referenceData.accounts }

private fun sourcesRoute(referenceData: ReferenceData) = "$BASE_URL/sources" meta {
    operationId = "$BASE_URL/sources"
    summary = "Get a list of sources"
    tags += tag
    returning(OK, referenceLens to listOf("String"))
} bindContract GET to referenceHandler { referenceData.sources }

private fun payees(referenceData: ReferenceData) = "$BASE_URL/payees" meta {
    operationId = "$BASE_URL/payees"
    summary = "Get a list of payees"
    tags += tag
    returning(OK, referenceLens to listOf("String"))
} bindContract GET to referenceHandler { referenceData.payees }

private fun descriptionsRoute(referenceData: ReferenceData) = "$BASE_URL/descriptions" meta {
    operationId = "$BASE_URL/descriptions"
    summary = "Get a list of description mappings"
    tags += descriptionsTag
    returning(
        OK,
        descriptionsLens to listOf(DescriptionMapping(FullDescription("String"), ShortDescription("String")))
    )
} bindContract GET to descriptionsHandler { referenceData.descriptions }

private fun multipleDescriptionsRoute(referenceData: ReferenceData) = "$BASE_URL/descriptions/multiple" meta {
    operationId = "$BASE_URL/descriptions/multiple"
    summary = "Post a list of description mappings"
    tags += descriptionsTag
    receiving(descriptionsLens to listOf(DescriptionMapping(FullDescription("String"), ShortDescription("String"))))
    returning(OK)
} bindContract POST to postDescriptionsHandler { referenceData.save(it) }
