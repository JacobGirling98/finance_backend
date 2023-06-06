package unit.domain

import domain.totalValue
import unit.fixtures.aDebitTransaction
import unit.fixtures.withAValueOf
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TransactionTest : DescribeSpec({

    describe("list of transactions") {
        it("should calculate total value of give transactions") {
            listOf(
                aDebitTransaction().withAValueOf(2.0),
                aDebitTransaction().withAValueOf(5.0)
            ).totalValue() shouldBe 7.0f
        }

        it("Should calculate value of single transaction") {
            listOf(aDebitTransaction().withAValueOf(2.0)).totalValue() shouldBe 2.0f
        }
    }
})