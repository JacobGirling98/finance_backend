package unit.resource

import dao.UUIDDatabase
import dao.asAuditableEntity
import dao.entityOf
import domain.Budget
import domain.BudgetReport
import domain.Category
import domain.DateRange
import domain.EndDate
import domain.StartDate
import domain.Value
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.withACategoryOf
import helpers.fixtures.withAValueOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import resource.BudgetCalculator
import resource.TransactionProcessor

class BudgetCalculatorTest : FunSpec({

    val transactionsProcessor = mockk<TransactionProcessor>()
    val budgetDatabase = mockk<UUIDDatabase<Budget>>()
    val budgetCalculator = spyk(BudgetCalculator(transactionsProcessor, budgetDatabase))

    test("creates report from transactions for given budget and date range") {
        val budget = Budget(Category("Food"), Value.of(50.0))
        val dateRange = DateRange(StartDate.of(2024, 1, 1), EndDate.of(2024, 2, 1))
        val transactions = listOf(entityOf(aDebitTransaction().withAValueOf(5.0).withACategoryOf("Food")))
        every { transactionsProcessor.transactionsBy(any(), any()) } returns transactions

        val report = budgetCalculator.calculateSpendingFor(budget, dateRange)

        report shouldBe BudgetReport(budget, dateRange, Value.of(5.0))
        verify { transactionsProcessor.transactionsBy(Category("Food"), dateRange) }
    }

    test("creates reports for all budgets for given date range") {
        val budget = Budget(Category("Food"), Value.of(50.0))
        val dateRange = DateRange(StartDate.of(2024, 1, 1), EndDate.of(2024, 2, 1))
        val report = BudgetReport(budget, dateRange, Value.of(5.0))
        every { budgetDatabase.selectAll() } returns listOf(budget.asAuditableEntity())
        every { budgetCalculator.calculateSpendingFor(any(), any()) } returns report

        val reports = budgetCalculator.calculateBudgets(dateRange)

        reports shouldBe listOf(BudgetReport(budget, dateRange, Value.of(5.0)))
        verify { budgetDatabase.selectAll() }
        verify { budgetCalculator.calculateSpendingFor(budget, dateRange) }
    }
})