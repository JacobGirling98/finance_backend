package integration

import config.mongoClient
import dao.mongo.Entity
import dao.mongo.StandingOrderCollection
import domain.*
import domain.Frequency.WEEKLY
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate


class StandingOrderCollectionTest : FunSpec({

    val standingOrderCollection = StandingOrderCollection(mongoClient)

    afterEach { deleteFrom("standingOrders") }

    test("can add and find by id") {
        val standingOrder = StandingOrder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            Category("Food"),
            Value.of(2.0),
            Description("Banana"),
            TransactionType.DEBIT,
            Outgoing(true),
            Quantity(1),
            Recipient("me"),
            Inbound("inbound"),
            Outbound("outbound"),
            Source("source")
        )
        val id = standingOrderCollection.add(standingOrder)

        id shouldNotBe null

        standingOrderCollection.findById(id!!)?.domain shouldBe standingOrder
    }

    test("null values are allowed") {
        val standingOrder = StandingOrder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            Category("Food"),
            Value.of(2.0),
            Description("Banana"),
            TransactionType.DEBIT,
            Outgoing(true),
            Quantity(1),
        )
        val id = standingOrderCollection.add(standingOrder)

        id shouldNotBe null

        with(standingOrderCollection.findById(id!!)?.domain) {
            this?.outbound shouldBe null
            this?.inbound shouldBe null
            this?.source shouldBe null
            this?.recipient shouldBe null
        }
    }

    test("can retrieve all standing orders") {
        val standingOrder = StandingOrder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            Category("Food"),
            Value.of(2.0),
            Description("Banana"),
            TransactionType.DEBIT,
            Outgoing(true),
            Quantity(1),
        )
        standingOrderCollection.add(standingOrder)
        standingOrderCollection.add(standingOrder)

        standingOrderCollection.findAll() shouldHaveSize 2
    }

    test("can update a standing order") {
        val standingOrder = StandingOrder(
            Date(LocalDate.of(2023, 1, 1)),
            Frequency.MONTHLY,
            Category("Food"),
            Value.of(2.0),
            Description("Banana"),
            TransactionType.DEBIT,
            Outgoing(true),
            Quantity(1),
        )
        val id = standingOrderCollection.add(standingOrder)

        standingOrderCollection.update(Entity(id!!, standingOrder.copy(frequency = WEEKLY)))

        with(standingOrderCollection.findById(id)?.domain) {
            this?.frequency shouldBe WEEKLY
            this?.category shouldBe Category("Food")
        }
    }
})