package unit.http.assembler

import domain.Category
import domain.Date
import domain.Description
import domain.Frequency
import domain.FrequencyQuantity
import domain.Inbound
import domain.Outbound
import domain.Outgoing
import domain.Quantity
import domain.Recipient
import domain.Source
import domain.TransactionType
import domain.Value
import http.assembler.standingOrderFrom
import http.model.StandingOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StandingOrdersTest : FunSpec({

    test("can create standing order from inbound credit request") {
        val request = StandingOrder.CreditDebit(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            Quantity(1)
        )

        val expected = domain.StandingOrder(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            TransactionType.CREDIT,
            Outgoing(true),
            Quantity(1)
        )

        standingOrderFrom(request, TransactionType.CREDIT) shouldBe expected
    }

    test("can create standing order from inbound debit request") {
        val request = StandingOrder.CreditDebit(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            Quantity(1)
        )

        val expected = domain.StandingOrder(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            TransactionType.DEBIT,
            Outgoing(true),
            Quantity(1)
        )

        standingOrderFrom(request, TransactionType.DEBIT) shouldBe expected
    }

    test("can create standing order from inbound bank transfer request") {
        val request = StandingOrder.BankTransfer(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            Quantity(1),
            Recipient("Me")
        )

        val expected = domain.StandingOrder(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            TransactionType.BANK_TRANSFER,
            Outgoing(true),
            Quantity(1),
            Recipient("Me")
        )

        standingOrderFrom(request) shouldBe expected
    }

    test("can create standing order from inbound personal transfer request") {
        val request = StandingOrder.PersonalTransfer(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            Outbound("Out"),
            Inbound("In")
        )

        val expected = domain.StandingOrder(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            TransactionType.PERSONAL_TRANSFER,
            Outgoing(false),
            Quantity(1),
            inbound = Inbound("In"),
            outbound = Outbound("Out")
        )

        standingOrderFrom(request) shouldBe expected
    }

    test("can create standing order from inbound income request") {
        val request = StandingOrder.Income(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            Source("Work")
        )

        val expected = domain.StandingOrder(
            Date.of(2020, 1, 1),
            FrequencyQuantity(1),
            Frequency.WEEKLY,
            Category("Food"),
            Value.of(5.0),
            Description("Food"),
            TransactionType.INCOME,
            Outgoing(false),
            Quantity(1),
            source = Source("Work")
        )

        standingOrderFrom(request) shouldBe expected
    }
})