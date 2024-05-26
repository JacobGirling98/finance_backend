package unit.resource

import domain.Date
import domain.DateRange
import domain.EndDate
import domain.StartDate
import domain.Value
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.aPersonalTransferTransaction
import helpers.fixtures.aWagesIncome
import helpers.fixtures.withADateOf
import helpers.fixtures.withAValueOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import resource.incomeBetween
import resource.mostRecent
import resource.netIncomeBetween
import resource.savingsBetween
import resource.spendingBetween
import java.time.LocalDate

class TransactionAnalyserTest : FunSpec({
    test("excludes inbound transactions") {
        val transactions = listOf(
            aDebitTransaction().withAValueOf(10.0).withADateOf(2021, 1, 1),
            aWagesIncome().withAValueOf(10.0).withADateOf(2021, 2, 1)
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
            aPersonalTransferTransaction().withAValueOf(20.0).withADateOf(2021, 3, 1)
        )
        transactions.incomeBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(10.0)
    }

    test("can sum incomes") {
        val transactions = listOf(
            aWagesIncome().withAValueOf(10.0).withADateOf(2021, 1, 1),
            aDebitTransaction().withAValueOf(15.0).withADateOf(2021, 2, 1),
            aWagesIncome().withAValueOf(20.0).withADateOf(2021, 3, 1)
        )
        transactions.incomeBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(30.0)
    }

    test("can sum savings") {
        val transactions = listOf(
            aPersonalTransferTransaction().withAValueOf(10.0).withADateOf(2021, 1, 1),
            aPersonalTransferTransaction().withAValueOf(15.0).withADateOf(2021, 2, 1),
            aWagesIncome().withAValueOf(30.0).withADateOf(2021, 3, 1),
            aDebitTransaction().withAValueOf(40.0).withADateOf(2021, 4, 1)
        )
        transactions.savingsBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(25.0)
    }

    test("can calculate net income") {
        val transactions = listOf(
            aWagesIncome().withAValueOf(30.0).withADateOf(2021, 3, 1),
            aDebitTransaction().withAValueOf(20.0).withADateOf(2021, 4, 1)
        )
        transactions.netIncomeBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(10.0)
    }

    test("net income ignores personal transfers") {
        val transactions = listOf(
            aPersonalTransferTransaction().withAValueOf(30.0).withADateOf(2021, 2, 1),
            aWagesIncome().withAValueOf(30.0).withADateOf(2021, 3, 1),
            aDebitTransaction().withAValueOf(20.0).withADateOf(2021, 4, 1)
        )
        transactions.netIncomeBetween(
            DateRange(
                StartDate.of(2021, 1, 1),
                EndDate.of(2022, 1, 1)
            )
        ) shouldBe Value.of(10.0)
    }

    test("can get most recent transaction") {
        val transactions = listOf(
            aDebitTransaction().withADateOf(LocalDate.of(2023, 1, 1)),
            aDebitTransaction().withADateOf(LocalDate.of(2023, 3, 1)),
            aDebitTransaction().withADateOf(LocalDate.of(2023, 2, 1))
        )
        transactions.mostRecent() shouldBe Date(LocalDate.of(2023, 3, 1))
    }
})
