package http.contract

import dao.Entity
import dao.entityOf
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import http.asTag
import http.handler.descriptionsHandler
import http.handler.postDescriptionsHandler
import http.handler.referenceHandler
import http.lense.descriptionEntitiesLens
import http.lense.descriptionsLens
import http.lense.referenceEntitiesLens
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import java.util.*

private const val BASE_URL = "/reference"
private val tag = BASE_URL.asTag()
private val descriptionsTag = "$BASE_URL/descriptions".asTag()

fun categoriesContract(getCategories: () -> List<Entity<String>>) = "$BASE_URL/categories" meta {
    operationId = "$BASE_URL/categories"
    summary = "Get a list of transaction categories"
    tags += tag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getCategories)

fun accountsContract(getAccounts: () -> List<Entity<String>>) = "$BASE_URL/accounts" meta {
    operationId = "$BASE_URL/categories"
    summary = "Get a list of accounts"
    tags += tag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getAccounts)

fun sourcesContract(getSources: () -> List<Entity<String>>) = "$BASE_URL/sources" meta {
    operationId = "$BASE_URL/sources"
    summary = "Get a list of sources"
    tags += tag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getSources)

fun payeesContract(getPayees: () -> List<Entity<String>>) = "$BASE_URL/payees" meta {
    operationId = "$BASE_URL/payees"
    summary = "Get a list of payees"
    tags += tag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getPayees)

fun getDescriptionsContract(getDescriptions: () -> List<Entity<DescriptionMapping>>) = "$BASE_URL/descriptions" meta {
    operationId = "$BASE_URL/descriptions"
    summary = "Get a list of description mappings"
    tags += descriptionsTag
    returning(
        OK,
        descriptionEntitiesLens to listOf(
            entityOf(
                DescriptionMapping(
                    FullDescription("String"),
                    ShortDescription("String")
                )
            )
        )
    )
} bindContract GET to descriptionsHandler(getDescriptions)

fun addDescriptionsContract(saveDescriptions: (List<DescriptionMapping>) -> List<UUID>) =
    "$BASE_URL/descriptions/multiple" meta {
        operationId = "$BASE_URL/descriptions/multiple"
        summary = "Post a list of description mappings"
        tags += descriptionsTag
        receiving(
            descriptionsLens to listOf(
                DescriptionMapping(
                    FullDescription("String"),
                    ShortDescription("String")
                )
            )
        )
        returning(OK)
    } bindContract POST to postDescriptionsHandler(saveDescriptions)
