package integration

import config.mongoClient
import dao.mongo.StandingOrderCollection
import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate
import java.util.*


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
            UUID.randomUUID(),
            Recipient("me"),
            Inbound("inbound"),
            Outbound("outbound"),
            Source("source")
        )
        val id = standingOrderCollection.add(standingOrder)

        id shouldNotBe null

        standingOrderCollection.findById(id!!) shouldBe standingOrder
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
            UUID.randomUUID(),
        )
        val id = standingOrderCollection.add(standingOrder)

        id shouldNotBe null

        with(standingOrderCollection.findById(id!!)) {
            this?.outbound shouldBe null
            this?.inbound shouldBe null
            this?.source shouldBe null
            this?.recipient shouldBe null
        }
    }
})