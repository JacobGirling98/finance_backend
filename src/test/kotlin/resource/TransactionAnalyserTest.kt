package resource

import domain.*
import domain.TransactionType.INCOME
import fixtures.aDebitTransaction
import fixtures.aWagesIncome
import fixtures.withADateOf
import fixtures.withAValueOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TransactionAnalyserTest : FunSpec({
    test("excludes inbound transactions") {
        val transactions = listOf(
            aDebitTransaction().withAValueOf(10.0).withADateOf(2021, 1, 1),
            aWagesIncome().withAValueOf(10.0).withADateOf(2021, 2, 1),
        )
        transactions.spendingBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(10.0)
    }

    test("can get transactions between two dates (inclusive and exclusive)") {
        val transactions = listOf(
            aDebitTransaction().withAValueOf(10.0).withADateOf(2020, 1, 1),
            aDebitTransaction().withAValueOf(10.0).withADateOf(2021, 1, 1),
            aDebitTransaction().withAValueOf(15.0).withADateOf(2021, 2, 1),
            aDebitTransaction().withAValueOf(10.0).withADateOf(2022, 1, 1)
        )
        transactions.spendingBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(25.0)
    }

    test("can get incomes") {
        val transactions = listOf(
            aWagesIncome().withAValueOf(10.0).withADateOf(2021, 1, 1),
            aDebitTransaction().withAValueOf(15.0).withADateOf(2021, 2, 1),
        )
        transactions.incomeBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(10.0)
    }
})

private fun List<Transaction>.valueBetween(dates: DateRange, filter: (_: Transaction) -> Boolean): Value =
    filter(dates).filter { filter(it) }.map { it.value }.reduce { acc, value -> acc.add(value) }

private fun List<Transaction>.spendingBetween(dates: DateRange): Value = valueBetween(dates) { it.outgoing.value }

private fun List<Transaction>.incomeBetween(dates: DateRange): Value = valueBetween(dates) { it.type == INCOME }