package http.contract

import dao.Entity
import dao.entityOf
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import http.asTag
import http.handler.addTextTypeHandler
import http.handler.descriptionsHandler
import http.handler.postDescriptionsHandler
import http.handler.referenceHandler
import http.lense.descriptionEntitiesLens
import http.lense.descriptionsLens
import http.lense.referenceEntitiesLens
import http.lense.stringLens
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import java.util.*

private const val BASE_URL = "/reference"

private val descriptionsTag = "$BASE_URL/descriptions".asTag()
private val categoriesTag = "$BASE_URL/categories".asTag()
private val accountsTag = "$BASE_URL/accounts".asTag()
private val sourcesTag = "$BASE_URL/sources".asTag()
private val payeesTag = "$BASE_URL/payees".asTag()

fun getCategoriesContract(getCategories: () -> List<Entity<String>>) = "$BASE_URL/categories" meta {
    operationId = "$BASE_URL/categories/get"
    summary = "Get a list of transaction categories"
    tags += categoriesTag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getCategories)

fun getAccountsContract(getAccounts: () -> List<Entity<String>>) = "$BASE_URL/accounts" meta {
    operationId = "$BASE_URL/accounts/get"
    summary = "Get a list of accounts"
    tags += accountsTag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getAccounts)

fun getSourcesContract(getSources: () -> List<Entity<String>>) = "$BASE_URL/sources" meta {
    operationId = "$BASE_URL/sources/get"
    summary = "Get a list of sources"
    tags += sourcesTag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getSources)

fun getPayeesContract(getPayees: () -> List<Entity<String>>) = "$BASE_URL/payees" meta {
    operationId = "$BASE_URL/payees/get"
    summary = "Get a list of payees"
    tags += payeesTag
    returning(OK, referenceEntitiesLens to listOf(entityOf("String")))
} bindContract GET to referenceHandler(getPayees)

fun getDescriptionsContract(getDescriptions: () -> List<Entity<DescriptionMapping>>) = "$BASE_URL/descriptions" meta {
    operationId = "$BASE_URL/descriptions/get"
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
        operationId = "$BASE_URL/descriptions/multiple/post"
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

fun addCategoryContact(addCategory: (String) -> UUID) = "$BASE_URL/categories" meta {
    operationId = "$BASE_URL/categories/post"
    summary = "Add a new category"
    tags += categoriesTag
    receiving(stringLens to "Category")
    returning(NO_CONTENT)
} bindContract POST to addTextTypeHandler(addCategory)

fun addAccountContact(addAccount: (String) -> UUID) = "$BASE_URL/accounts" meta {
    operationId = "$BASE_URL/accounts/post"
    summary = "Add a new account"
    tags += accountsTag
    receiving(stringLens to "Account")
    returning(NO_CONTENT)
} bindContract POST to addTextTypeHandler(addAccount)

fun addSourceContact(addSource: (String) -> UUID) = "$BASE_URL/sources" meta {
    operationId = "$BASE_URL/sources/post"
    summary = "Add a new source"
    tags += sourcesTag
    receiving(stringLens to "Source")
    returning(NO_CONTENT)
} bindContract POST to addTextTypeHandler(addSource)

fun addPayeesContact(addPayee: (String) -> UUID) = "$BASE_URL/payee" meta {
    operationId = "$BASE_URL/payees/post"
    summary = "Add a new payee"
    tags += payeesTag
    receiving(stringLens to "Payees")
    returning(NO_CONTENT)
} bindContract POST to addTextTypeHandler(addPayee)
