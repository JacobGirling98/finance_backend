package unit.domain

import domain.totalValue
import helpers.fixtures.aDebitTransaction
import helpers.fixtures.withACategoryOf
import helpers.fixtures.withADescriptionOf
import helpers.fixtures.withARecipientOf
import helpers.fixtures.withAValueOf
import helpers.fixtures.withAnInboundAccountOf
import helpers.fixtures.withAnIncomeSourceOf
import helpers.fixtures.withAnOutboundAccountOf
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

    describe("any match") {
        it("for description") {
            aDebitTransaction().withADescriptionOf("testing").anyMatch("est") shouldBe true
            aDebitTransaction().withADescriptionOf("testing").anyMatch("EST") shouldBe true
            aDebitTransaction().withADescriptionOf("testing").anyMatch("match") shouldBe false
        }

        it("for category") {
            aDebitTransaction().withACategoryOf("food").anyMatch("food") shouldBe true
            aDebitTransaction().withACategoryOf("food").anyMatch("FOOD") shouldBe true
            aDebitTransaction().withACategoryOf("food").anyMatch("match") shouldBe false
        }

        it("for recipient") {
            aDebitTransaction().withARecipientOf("friend").anyMatch("iend") shouldBe true
            aDebitTransaction().withARecipientOf("friend").anyMatch("IEND") shouldBe true
            aDebitTransaction().withARecipientOf("friend").anyMatch("match") shouldBe false
        }

        it("for inbound account") {
            aDebitTransaction().withAnInboundAccountOf("current").anyMatch("urren") shouldBe true
            aDebitTransaction().withAnInboundAccountOf("current").anyMatch("URREN") shouldBe true
            aDebitTransaction().withAnInboundAccountOf("current").anyMatch("match") shouldBe false
        }

        it("for outbound account") {
            aDebitTransaction().withAnOutboundAccountOf("current").anyMatch("urren") shouldBe true
            aDebitTransaction().withAnOutboundAccountOf("current").anyMatch("URREN") shouldBe true
            aDebitTransaction().withAnOutboundAccountOf("current").anyMatch("match") shouldBe false
        }

        it("for income source") {
            aDebitTransaction().withAnIncomeSourceOf("work").anyMatch("work") shouldBe true
            aDebitTransaction().withAnIncomeSourceOf("work").anyMatch("WORK") shouldBe true
            aDebitTransaction().withAnIncomeSourceOf("work").anyMatch("match") shouldBe false
        }
    }
})
