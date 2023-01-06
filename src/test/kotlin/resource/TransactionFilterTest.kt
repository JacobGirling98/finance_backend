package resource

import domain.DateRange
import domain.EndDate
import domain.StartDate
import domain.Transaction
import fixtures.aDebitTransaction
import fixtures.withADateOf
import fixtures.withADescriptionOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class TransactionFilterTest : FunSpec({
    test("can filter transactions") {
        val transactions = listOf(
            aDebitTransaction().withADescriptionOf("should be included").withADateOf(2020, 1, 10),
            aDebitTransaction().withADescriptionOf("should not be included").withADateOf(2020, 2, 10),
        )

        transactions.filter(
            DateRange(
                StartDate(2020, 1, 1),
                EndDate(2020, 2, 1)
            )
        )
            .shouldContainDescription("should be included")
            .shouldNotContainDescription("should not be included")
    }

    test("start date in inclusive") {
        val transactions = listOf(
            aDebitTransaction().withADescriptionOf("should be included").withADateOf(2020, 1, 1),
            aDebitTransaction().withADescriptionOf("should not be included").withADateOf(2020, 2, 10),
        )

        transactions.filter(
            DateRange(
                StartDate(2020, 1, 1),
                EndDate(2020, 2, 1)
            )
        )
            .shouldContainDescription("should be included")
            .shouldNotContainDescription("should not be included")
    }

    test("end date is exclusive") {
        val transactions = listOf(
            aDebitTransaction().withADescriptionOf("should be included").withADateOf(2020, 1, 10),
            aDebitTransaction().withADescriptionOf("should not be included").withADateOf(2020, 2, 1),
        )

        transactions.filter(
            DateRange(
                StartDate(2020, 1, 1),
                EndDate(2020, 2, 1)
            )
        )
            .shouldContainDescription("should be included")
            .shouldNotContainDescription("should not be included")
    }
})

private fun containDescription(description: String) = Matcher<List<Transaction>> { value ->
    MatcherResult(
        value.any { it.description.value == description },
        { "transactions should have contained a transaction with a description of $description" },
        { "transactions should not be missing a transaction with a description of $description" }
    )
}

private fun List<Transaction>.shouldContainDescription(description: String): List<Transaction> {
    this should containDescription(description)
    return this
}

private fun List<Transaction>.shouldNotContainDescription(description: String): List<Transaction> {
    this shouldNot containDescription(description)
    return this
}
