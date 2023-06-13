package unit.resource

import dao.TransactionsDatabase
import dao.mongo.StandingOrderCollection
import domain.Date
import domain.Frequency
import domain.Transaction
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.mockk
import io.mockk.verify
import resource.StandingOrderProcessor
import unit.common.Factory
import java.time.LocalDate

class StandingOrderProcessorTest : FunSpec({

    val now = { LocalDate.of(2020, 1, 1) }
    val monthBeforeNow = LocalDate.of(2019, 12, 2)
    val monthAfterNow = LocalDate.of(2020, 1, 31)
    val weekBeforeNow = LocalDate.of(2019, 12, 27)

    val uuid = "12345"

    val standingOrdersDatabase = mockk<StandingOrderCollection>(relaxed = true)
    val transactionsDatabase = TransactionsDatabaseTestDouble()
    val processor = StandingOrderProcessor(standingOrdersDatabase, transactionsDatabase, now)

    test("will not process standing order if current date is before standing order date") {
        processor.process(Factory(date = Date(monthAfterNow)).standingOrderEntity())

        verify(exactly = 0) { standingOrdersDatabase.update(any()) }
    }

    test("will process monthly standing order if current date is after standing order date") {
        val factory = Factory(date = Date(monthBeforeNow), id = uuid)
        val expected = Factory(date = Date(monthBeforeNow.plusMonths(1)), id = uuid).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        transactionsDatabase.transactionsSaved shouldHaveSingleElement factory.transaction()
        verify { standingOrdersDatabase.update(expected) }
    }

    test("will process standing order if current date is equal to standing order date") {
        val factory = Factory(date = Date(now()), id = uuid)
        val expected = Factory(date = Date(now().plusMonths(1)), id = uuid).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        transactionsDatabase.transactionsSaved shouldHaveSingleElement factory.transaction()
        verify { standingOrdersDatabase.update(expected) }
    }

    test("will process monthly standing order multiple times if current date is multiple time periods after standing order date") {
        val firstFactory = Factory(date = Date(monthBeforeNow.minusMonths(1)), id = uuid)
        val secondFactory = Factory(date = Date(monthBeforeNow), id = uuid)
        val thirdFactory = Factory(date = Date(monthBeforeNow.plusMonths(1)), id = uuid)

        processor.process(firstFactory.standingOrderEntity())

        transactionsDatabase.transactionsSaved
            .shouldHaveSize(2)
            .shouldContain(firstFactory.transaction())
            .shouldContain(secondFactory.transaction())
        verify { standingOrdersDatabase.update(secondFactory.standingOrderEntity()) }
        verify { standingOrdersDatabase.update(thirdFactory.standingOrderEntity()) }
    }

    test("will process weekly standing order if current date is after standing order date") {
        val factory = Factory(date = Date(weekBeforeNow), frequency = Frequency.WEEKLY, id = uuid)
        val expected = Factory(date = Date(weekBeforeNow.plusWeeks(1)), frequency = Frequency.WEEKLY, id = uuid).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        transactionsDatabase.transactionsSaved shouldHaveSingleElement factory.transaction()
        verify { standingOrdersDatabase.update(expected) }
    }

    test("will process weekly standing order multiple times if current date is multiple time periods after standing order date") {
        val firstFactory = Factory(date = Date(weekBeforeNow.minusWeeks(1)), frequency = Frequency.WEEKLY, id = uuid)
        val secondFactory = Factory(date = Date(weekBeforeNow), frequency = Frequency.WEEKLY, id = uuid)
        val thirdFactory = Factory(date = Date(weekBeforeNow.plusWeeks(1)), frequency = Frequency.WEEKLY, id = uuid)

        processor.process(firstFactory.standingOrderEntity())

        transactionsDatabase.transactionsSaved
            .shouldHaveSize(2)
            .shouldContain(firstFactory.transaction())
            .shouldContain(secondFactory.transaction())
        verify { standingOrdersDatabase.update(secondFactory.standingOrderEntity()) }
        verify { standingOrdersDatabase.update(thirdFactory.standingOrderEntity()) }
    }

    test("will process all standing orders") {
        val firstFactory = Factory(date = Date(monthBeforeNow))
        val secondFactory = Factory(date = Date(weekBeforeNow), frequency = Frequency.WEEKLY)

        processor.processAll(listOf(firstFactory.standingOrderEntity(), secondFactory.standingOrderEntity()))

        val firstStandingOrder = firstFactory.standingOrderEntity()
            .copy(domain = firstFactory.standingOrder().copy(nextDate = Date(firstFactory.standingOrder().nextDate.value.plusMonths(1))))
        val secondStandingOrder = secondFactory.standingOrderEntity()
            .copy(domain = secondFactory.standingOrder().copy(nextDate = Date(secondFactory.standingOrder().nextDate.value.plusWeeks(1))))

        transactionsDatabase.transactionsSaved
            .shouldHaveSize(2)
            .shouldContain(firstFactory.transaction())
            .shouldContain(secondFactory.transaction())
        verify { standingOrdersDatabase.update(firstStandingOrder) }
        verify { standingOrdersDatabase.update(secondStandingOrder) }
    }
})

private class TransactionsDatabaseTestDouble : TransactionsDatabase("tmp") {
    var transactionsSaved = mutableListOf<Transaction>()

    override fun save(data: Transaction) {
        transactionsSaved.add(data)
    }
}