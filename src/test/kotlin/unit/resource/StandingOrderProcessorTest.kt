package unit.resource

import dao.Database
import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify
import resource.StandingOrderProcessor
import unit.common.Factory
import java.time.LocalDate
import java.util.*

class StandingOrderProcessorTest : FunSpec({

    val now = { LocalDate.of(2020, 1, 1) }
    val monthBeforeNow = LocalDate.of(2019, 12, 2)
    val monthAfterNow = LocalDate.of(2020, 1, 31)
    val weekBeforeNow = LocalDate.of(2019, 12, 27)

    val uuid = UUID.randomUUID()

    val standingOrderDatabase = mockk<Database<StandingOrder, UUID>>(relaxed = true)
    val transactionsDatabase = mockk<Database<Transaction, UUID>>(relaxed = true)
    val processor = StandingOrderProcessor(standingOrderDatabase, transactionsDatabase, now)

    test("will not process standing order if current date is before standing order date") {
        processor.process(Factory(date = Date(monthAfterNow)).standingOrderEntity())

        verify(exactly = 0) { standingOrderDatabase.update(any()) }
    }

    test("will process monthly standing order if current date is after standing order date") {
        val factory = Factory(date = Date(monthBeforeNow), id = uuid, addedBy = AddedBy("standing-order-processor"))
        val expected = Factory(date = Date(monthBeforeNow.plusMonths(1)), id = uuid).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        verify { transactionsDatabase.save(factory.transaction()) }
        verify { standingOrderDatabase.update(expected) }
    }

    test("will process standing order if current date is equal to standing order date") {
        val factory = Factory(date = Date(now()), id = uuid, addedBy = AddedBy("standing-order-processor"))
        val expected = Factory(date = Date(now().plusMonths(1)), id = uuid).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        verify { transactionsDatabase.save(factory.transaction()) }
        verify { standingOrderDatabase.update(expected) }
    }

    test("will process monthly standing order multiple times if current date is multiple time periods after standing order date") {
        val firstFactory = Factory(
            date = Date(monthBeforeNow.minusMonths(1)),
            id = uuid,
            addedBy = AddedBy("standing-order-processor")
        )
        val secondFactory =
            Factory(date = Date(monthBeforeNow), id = uuid, addedBy = AddedBy("standing-order-processor"))
        val thirdFactory = Factory(date = Date(monthBeforeNow.plusMonths(1)), id = uuid)

        processor.process(firstFactory.standingOrderEntity())

        verify { transactionsDatabase.save(firstFactory.transaction()) }
        verify { transactionsDatabase.save(secondFactory.transaction()) }
        verify { standingOrderDatabase.update(secondFactory.standingOrderEntity()) }
        verify { standingOrderDatabase.update(thirdFactory.standingOrderEntity()) }
    }

    test("will process weekly standing order if current date is after standing order date") {
        val factory = Factory(
            date = Date(weekBeforeNow),
            frequency = Frequency.WEEKLY,
            id = uuid,
            addedBy = AddedBy("standing-order-processor")
        )
        val expected = Factory(
            date = Date(weekBeforeNow.plusWeeks(1)),
            frequency = Frequency.WEEKLY,
            id = uuid
        ).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        verify { transactionsDatabase.save(factory.transaction()) }
        verify { standingOrderDatabase.update(expected) }
    }

    test("will process weekly standing order multiple times if current date is multiple time periods after standing order date") {
        val firstFactory = Factory(
            date = Date(weekBeforeNow.minusWeeks(1)),
            frequency = Frequency.WEEKLY,
            id = uuid,
            addedBy = AddedBy("standing-order-processor")
        )
        val secondFactory = Factory(
            date = Date(weekBeforeNow),
            frequency = Frequency.WEEKLY,
            id = uuid,
            addedBy = AddedBy("standing-order-processor")
        )
        val thirdFactory = Factory(
            date = Date(weekBeforeNow.plusWeeks(1)),
            frequency = Frequency.WEEKLY,
            id = uuid,
            addedBy = AddedBy("standing-order-processor")
        )

        processor.process(firstFactory.standingOrderEntity())

        verify { transactionsDatabase.save(firstFactory.transaction()) }
        verify { transactionsDatabase.save(secondFactory.transaction()) }
        verify { standingOrderDatabase.update(secondFactory.standingOrderEntity()) }
        verify { standingOrderDatabase.update(thirdFactory.standingOrderEntity()) }
    }

    test("will process all standing orders") {
        val firstFactory = Factory(date = Date(monthBeforeNow), addedBy = AddedBy("standing-order-processor"))
        val secondFactory = Factory(
            date = Date(weekBeforeNow),
            frequency = Frequency.WEEKLY,
            addedBy = AddedBy("standing-order-processor")
        )

        processor.processAll(listOf(firstFactory.standingOrderEntity(), secondFactory.standingOrderEntity()))

        val firstStandingOrder = firstFactory.standingOrderEntity()
            .copy(
                domain = firstFactory.standingOrder()
                    .copy(date = Date(firstFactory.standingOrder().date.value.plusMonths(1)))
            )
        val secondStandingOrder = secondFactory.standingOrderEntity()
            .copy(
                domain = secondFactory.standingOrder()
                    .copy(date = Date(secondFactory.standingOrder().date.value.plusWeeks(1)))
            )

        verify { transactionsDatabase.save(firstFactory.transaction()) }
        verify { transactionsDatabase.save(secondFactory.transaction()) }
        verify { standingOrderDatabase.update(firstStandingOrder) }
        verify { standingOrderDatabase.update(secondStandingOrder) }
    }

    test("can handle different frequency quantities") {
        val factory = Factory(
            date = Date(monthBeforeNow),
            id = uuid,
            frequencyQuantity = FrequencyQuantity(2),
            addedBy = AddedBy("standing-order-processor")
        )
        val expected = Factory(
            date = Date(monthBeforeNow.plusMonths(2)),
            id = uuid,
            frequencyQuantity = FrequencyQuantity(2)
        ).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        verify { transactionsDatabase.save(factory.transaction()) }
        verify { standingOrderDatabase.update(expected) }
    }

    test("added by uses 'standing-order-processor'") {
        val factory = Factory(date = Date(monthBeforeNow), id = uuid, addedBy = AddedBy("standing-order-processor"))
        val expected = Factory(
            date = Date(monthBeforeNow.plusMonths(1)),
            id = uuid
        ).standingOrderEntity()

        processor.process(factory.standingOrderEntity())

        verify { transactionsDatabase.save(factory.transaction()) }
        verify { standingOrderDatabase.update(expected) }
    }
})
