package config.contract

import dao.Entity
import domain.DateRange
import domain.EndDate
import domain.StartDate
import domain.Transaction
import http.asTag
import http.handler.dateRangeHandler
import http.lense.dateRangeListLens
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import resource.fiscalMonthsOf
import resource.fiscalYearsOf
import resource.monthsOf
import resource.yearsOf

private const val REFERENCE_URL = "/reference"
private val referenceTag = REFERENCE_URL.asTag()

fun dateRangeContracts(data: () -> List<Entity<Transaction>>): List<ContractRoute> {
    return listOf(
        monthsRoute(data),
        yearsRoute(data),
        fiscalMonthsRoute(data),
        fiscalYearsRoute(data)
    )
}

private fun monthsRoute(data: () -> List<Entity<Transaction>>) = "$REFERENCE_URL/months" meta {
    operationId = "$REFERENCE_URL/months"
    summary = "Get a list of months"
    tags += referenceTag
    returning(OK, dateRangeListLens to listOf(DateRange(StartDate.of(2020, 1, 1), EndDate.of(2020, 2, 1))))
} bindContract GET to dateRangeHandler(monthsOf(data))

private fun yearsRoute(data: () -> List<Entity<Transaction>>) = "$REFERENCE_URL/years" meta {
    operationId = "$REFERENCE_URL/years"
    summary = "Get a list of years"
    tags += referenceTag
    returning(OK, dateRangeListLens to listOf(DateRange(StartDate.of(2020, 1, 1), EndDate.of(2021, 1, 1))))
} bindContract GET to dateRangeHandler(yearsOf(data))

private fun fiscalMonthsRoute(data: () -> List<Entity<Transaction>>) = "$REFERENCE_URL/fiscal-months" meta {
    operationId = "$REFERENCE_URL/fiscal-months"
    summary = "Get a list of fiscal months"
    tags += referenceTag
    returning(OK, dateRangeListLens to listOf(DateRange(StartDate.of(2020, 1, 15), EndDate.of(2020, 2, 15))))
} bindContract GET to dateRangeHandler(fiscalMonthsOf(data))

private fun fiscalYearsRoute(data: () -> List<Entity<Transaction>>) = "$REFERENCE_URL/fiscal-years" meta {
    operationId = "$REFERENCE_URL/fiscal-years"
    summary = "Get a list of fiscal years"
    tags += referenceTag
    returning(OK, dateRangeListLens to listOf(DateRange(StartDate.of(2020, 1, 15), EndDate.of(2021, 1, 15))))
} bindContract GET to dateRangeHandler(fiscalYearsOf(data))
