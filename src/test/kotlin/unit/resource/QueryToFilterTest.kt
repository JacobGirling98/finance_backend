package unit.resource

import dao.Entity
import dao.entityOf
import domain.EndDate
import domain.StartDate
import domain.Transaction
import domain.TransactionType
import helpers.fixtures.aCreditTransaction
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.withADateOf
import helpers.matchers.shouldContainDomain
import helpers.matchers.shouldNotContainDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import java.time.LocalDate

class QueryToFilterTest : FunSpec({

    test("no queries throws an error") {
        shouldThrow<IllegalStateException> {
            toFilter(
                to = null,
                from = null,
                type = null
            )
        }

    }

    test("only from date provided gives all transactions after it") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2023, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2024, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2025, 1, 1)),
        )

        val filter = toFilter(from = StartDate(LocalDate.of(2023, 6, 1)))

        transactions.filter(filter).let { filtered ->
            filtered shouldContainDomain aDebitTransaction().withADateOf(2024, 1, 1)
            filtered shouldContainDomain aDebitTransaction().withADateOf(2025, 1, 1)
            filtered shouldNotContainDomain aDebitTransaction().withADateOf(2023, 1, 1)

        }
    }

    test("from date is inclusive") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2023, 1, 1)),
        )

        val filter = toFilter(from = StartDate(LocalDate.of(2023, 1, 1)))

        transactions.filter(filter) shouldContainDomain aDebitTransaction().withADateOf(2023, 1, 1)
    }

    test("only to date provided gives all transactions before it") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2023, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2024, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2025, 1, 1)),
        )

        val filter = toFilter(to = EndDate(LocalDate.of(2024, 6, 1)))

        transactions.filter(filter).let { filtered ->
            filtered shouldContainDomain aDebitTransaction().withADateOf(2023, 1, 1)
            filtered shouldContainDomain aDebitTransaction().withADateOf(2024, 1, 1)
            filtered shouldNotContainDomain aDebitTransaction().withADateOf(2025, 1, 1)
        }
    }

    test("from date is exclusive") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2023, 1, 1)),
        )

        val filter = toFilter(to = EndDate(LocalDate.of(2023, 1, 1)))

        transactions.filter(filter) shouldNotContainDomain aDebitTransaction().withADateOf(2023, 1, 1)
    }

    test("returns dates between the two given") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2023, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2024, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2025, 1, 1)),
        )

        val filter = toFilter(
            to = EndDate(LocalDate.of(2024, 6, 1)),
            from = StartDate(LocalDate.of(2023, 6, 1))
        )

        transactions.filter(filter).let { filtered ->
            filtered shouldNotContainDomain aDebitTransaction().withADateOf(2023, 1, 1)
            filtered shouldContainDomain aDebitTransaction().withADateOf(2024, 1, 1)
            filtered shouldNotContainDomain aDebitTransaction().withADateOf(2025, 1, 1)
        }
    }

    test("can filter by transaction type") {
        val transactions = listOf(
            entityOf(aDebitTransaction()),
            entityOf(aCreditTransaction()),
        )

        val filter = toFilter(
            type = TransactionType.CREDIT
        )

        transactions.filter(filter).let { filtered ->
            filtered shouldContainDomain aCreditTransaction()
            filtered shouldNotContainDomain aDebitTransaction()
        }
    }

    test("can filter by everything") {
        val transactions = listOf(
            entityOf(aDebitTransaction().withADateOf(2023, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2024, 1, 1)),
            entityOf(aCreditTransaction().withADateOf(2024, 1, 1)),
            entityOf(aDebitTransaction().withADateOf(2025, 1, 1)),
        )

        val filter = toFilter(
            to = EndDate(LocalDate.of(2024, 6, 1)),
            from = StartDate(LocalDate.of(2023, 6, 1)),
            type = TransactionType.DEBIT
        )

        transactions.filter(filter).let { filtered ->
            filtered shouldNotContainDomain aDebitTransaction().withADateOf(2023, 1, 1)
            filtered shouldContainDomain aDebitTransaction().withADateOf(2024, 1, 1)
            filtered shouldNotContainDomain aCreditTransaction().withADateOf(2024, 1, 1)
            filtered shouldNotContainDomain aDebitTransaction().withADateOf(2025, 1, 1)
        }
    }
})

fun toFilter(
    to: EndDate? = null,
    from: StartDate? = null,
    type: TransactionType? = null
): (Entity<Transaction>) -> Boolean {
    if (to == null && from == null && type == null) throw IllegalStateException("At least one filter should be provided")

    val fromCondition: (Entity<Transaction>) -> Boolean = if (from != null) {
        { it.domain.date.value.isAfter(from.value) || it.domain.date.value == from.value }
    } else {
        { true }
    }

    val toCondition: (Entity<Transaction>) -> Boolean = if (to != null) {
        { it.domain.date.value.isBefore(to.value) }
    } else {
        { true }
    }

    val typeCondition: (Entity<Transaction>) -> Boolean = if (type != null) {
        { it.domain.type == type }
    } else {
        { true }
    }

    return { fromCondition(it) && toCondition(it) && typeCondition(it) }
}
