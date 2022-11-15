package finance.http.assembler

import finance.domain.*
import finance.http.model.BankTransfer
import finance.http.model.CreditDebit
import finance.http.model.Income
import finance.http.model.PersonalTransfer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDate

class TransactionsTest : FunSpec({

    test("convert credit dto to transaction") {
        transactionFrom(
            CreditDebit(
                Date(LocalDate.MIN),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Grapes"),
                Quantity(1)
            ), TransactionType.CREDIT
        ) shouldBe Transaction(
            Date(LocalDate.MIN),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Grapes"),
            TransactionType.CREDIT,
            Outgoing(true),
            Quantity(1)
        )
    }

    test("convert bank transfer dto to transaction") {
        transactionFrom(
            BankTransfer(
                Date(LocalDate.MIN),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Grapes"),
                Quantity(1),
                Recipient("Friend")
            )
        ) shouldBe Transaction(
            Date(LocalDate.MIN),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Grapes"),
            TransactionType.BANK_TRANSFER,
            Outgoing(true),
            Quantity(1),
            recipient = Recipient("Friend")
        )
    }

    test("convert personal transfer dto to transaction") {
        transactionFrom(
            PersonalTransfer(
                Date(LocalDate.MIN),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Grapes"),
                Outbound("Current"),
                Inbound("Savings")
            )
        ) shouldBe Transaction(
            Date(LocalDate.MIN),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Grapes"),
            TransactionType.PERSONAL_TRANSFER,
            Outgoing(false),
            Quantity(1),
            outbound = Outbound("Current"),
            inbound = Inbound("Savings")
        )
    }

    test("convert income dto to transaction") {
        transactionFrom(
            Income(
                Date(LocalDate.MIN),
                Category("Food"),
                Value(BigDecimal("12.50")),
                Description("Grapes"),
                Source("Work")
            )
        ) shouldBe Transaction(
            Date(LocalDate.MIN),
            Category("Food"),
            Value(BigDecimal("12.50")),
            Description("Grapes"),
            TransactionType.INCOME,
            Outgoing(false),
            Quantity(1),
            source = Source("Work")
        )
    }
})