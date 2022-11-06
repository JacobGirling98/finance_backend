package resource

import common.Factory
import dao.StandingOrdersDatabase
import dao.TransactionsDatabase
import domain.Date
import domain.Frequency
import domain.StandingOrder
import domain.Transaction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class StandingOrderProcessorTest : FunSpec({

    val now = LocalDate.of(2020, 1, 1)
    val monthBeforeNow = LocalDate.of(2019, 12, 2)
    val monthAfterNow = LocalDate.of(2020, 1, 31)
    val weekBeforeNow = LocalDate.of(2019, 12, 27)

    val standingOrdersDatabase = StandingOrdersDatabaseTestDouble()
    val transactionsDatabase = TransactionsDatabaseTestDouble()
    val processor = StandingOrderProcessor(standingOrdersDatabase, transactionsDatabase, now)

    test("will not process standing order if current date is before standing order date") {
        processor.process(Factory(date = Date(monthAfterNow)).standingOrder())

        standingOrdersDatabase.ordersUpdated.shouldBeEmpty()
    }

    test("will process monthly standing order if current date is after standing order date") {
        val factory = Factory(date = Date(monthBeforeNow))
        val expected = Factory(date = Date(monthBeforeNow.plusMonths(1))).standingOrder()

        processor.process(factory.standingOrder())

        transactionsDatabase.transactionsSaved shouldHaveSingleElement factory.transaction()
        standingOrdersDatabase.ordersUpdated shouldHaveSingleElement expected
    }

    test("will process standing order if current date is equal to standing order date") {
        val factory = Factory(date = Date(now))
        val expected = Factory(date = Date(now.plusMonths(1))).standingOrder()

        processor.process(factory.standingOrder())

        transactionsDatabase.transactionsSaved shouldHaveSingleElement factory.transaction()
        standingOrdersDatabase.ordersUpdated shouldHaveSingleElement expected
    }

    test("will process monthly standing order multiple times if current date is multiple time periods after standing order date") {
        val firstFactory = Factory(date = Date(monthBeforeNow.minusMonths(1)))
        val secondFactory = Factory(date = Date(monthBeforeNow))
        val thirdFactory = Factory(date = Date(monthBeforeNow.plusMonths(1)))

        processor.process(firstFactory.standingOrder())

        transactionsDatabase.transactionsSaved
            .shouldHaveSize(2)
            .shouldContain(firstFactory.transaction())
            .shouldContain(secondFactory.transaction())
        standingOrdersDatabase.ordersUpdated
            .shouldHaveSize(2)
            .shouldContain(secondFactory.standingOrder())
            .shouldContain(thirdFactory.standingOrder())
    }

    test("will process weekly standing order if current date is after standing order date") {
        val factory = Factory(date = Date(weekBeforeNow), frequency = Frequency.WEEKLY)
        val expected = Factory(date = Date(weekBeforeNow.plusWeeks(1)), frequency = Frequency.WEEKLY).standingOrder()

        processor.process(factory.standingOrder())

        transactionsDatabase.transactionsSaved shouldHaveSingleElement factory.transaction()
        standingOrdersDatabase.ordersUpdated shouldHaveSingleElement expected
    }

    test("will process weekly standing order multiple times if current date is multiple time periods after standing order date") {
        val firstFactory = Factory(date = Date(weekBeforeNow.minusWeeks(1)), frequency = Frequency.WEEKLY)
        val secondFactory = Factory(date = Date(weekBeforeNow), frequency = Frequency.WEEKLY)
        val thirdFactory = Factory(date = Date(weekBeforeNow.plusWeeks(1)), frequency = Frequency.WEEKLY)

        processor.process(firstFactory.standingOrder())

        transactionsDatabase.transactionsSaved
            .shouldHaveSize(2)
            .shouldContain(firstFactory.transaction())
            .shouldContain(secondFactory.transaction())
        standingOrdersDatabase.ordersUpdated
            .shouldHaveSize(2)
            .shouldContain(secondFactory.standingOrder())
            .shouldContain(thirdFactory.standingOrder())
    }

    test("will process all standing orders") {
        val firstFactory = Factory(date = Date(monthBeforeNow))
        val secondFactory = Factory(date = Date(weekBeforeNow), frequency = Frequency.WEEKLY)

        processor.processAll(listOf(firstFactory.standingOrder(), secondFactory.standingOrder()))

        transactionsDatabase.transactionsSaved
            .shouldHaveSize(2)
            .shouldContain(firstFactory.transaction())
            .shouldContain(secondFactory.transaction())
        standingOrdersDatabase.ordersUpdated
            .shouldHaveSize(2)
            .shouldContain(
                firstFactory.standingOrder()
                    .copy(nextDate = Date(firstFactory.standingOrder().nextDate.value.plusMonths(1)))
            )
            .shouldContain(
                secondFactory.standingOrder()
                    .copy(nextDate = Date(secondFactory.standingOrder().nextDate.value.plusWeeks(1)))
            )
        standingOrdersDatabase.flushes shouldBe 1
    }
})

private class StandingOrdersDatabaseTestDouble : StandingOrdersDatabase("tmp") {
    val ordersUpdated = mutableListOf<StandingOrder>()
    var flushes = 0

    override fun update(id: Int, data: StandingOrder) {
        ordersUpdated.add(data)
    }

    override fun flush() {
        flushes++
    }

}

private class TransactionsDatabaseTestDouble : TransactionsDatabase("tmp") {
    var transactionsSaved = mutableListOf<Transaction>()

    override fun save(data: Transaction) {
        transactionsSaved.add(data)
    }
}