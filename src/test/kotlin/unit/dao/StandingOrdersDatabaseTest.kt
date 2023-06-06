package unit.dao

import dao.StandingOrdersDatabase
import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import unit.common.Factory
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class StandingOrdersDatabaseTest : FunSpec({

    val database = StandingOrdersDatabase(tmp)

    val firstId = UUID.randomUUID()
    val secondId = UUID.randomUUID()
    val thirdId = UUID.randomUUID()

    beforeEach {
        File(tmp).deleteRecursively()
        File(tmp).mkdirs()
    }

    afterEach {
        File(tmp).deleteRecursively()
    }

    test("can read from csv") {
        file().writeText(
            """
                next_date,frequency,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,id
                2022-11-01,monthly,,True,5.00,Bank Transfer,,,Recipient,,Spotify,Spotify,1,$firstId
                2022-11-16,weekly,,False,200.00,Personal Transfer,Current,Savings,,,Savings,Savings,1,$secondId
                2022-11-01,monthly,,True,13.00,Debit,,,,,Bill,Food,1,$thirdId
                
            """.trimIndent()
        )

        database.read()

        database.data
            .shouldContain(
                StandingOrder(
                    Date(LocalDate.of(2022, 11, 1)),
                    Frequency.MONTHLY,
                    Category("Spotify"),
                    Value(BigDecimal("5.00")),
                    Description("Spotify"),
                    TransactionType.BANK_TRANSFER,
                    Outgoing(true),
                    Quantity(1),
                    firstId,
                    Recipient("Recipient"),
                    null,
                    null,
                    null
                )
            )
            .shouldContain(
                StandingOrder(
                    Date(LocalDate.of(2022, 11, 16)),
                    Frequency.WEEKLY,
                    Category("Savings"),
                    Value(BigDecimal("200.00")),
                    Description("Savings"),
                    TransactionType.PERSONAL_TRANSFER,
                    Outgoing(false),
                    Quantity(1),
                    secondId,
                    null,
                    Inbound("Savings"),
                    Outbound("Current"),
                    null
                )
            )
            .shouldContain(
                StandingOrder(
                    Date(LocalDate.of(2022, 11, 1)),
                    Frequency.MONTHLY,
                    Category("Food"),
                    Value(BigDecimal("13.00")),
                    Description("Bill"),
                    TransactionType.DEBIT,
                    Outgoing(true),
                    Quantity(1),
                    thirdId,
                    null,
                    null,
                    null,
                    null
                )
            )
    }

    test("can flush to file without changes") {
        val initialContents = """
                next_date,frequency,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,id
                2022-11-01,monthly,,True,5.00,Bank Transfer,,,Recipient,,Spotify,Spotify,1,$firstId
                2022-11-16,weekly,,False,200.00,Personal Transfer,Current,Savings,,,Savings,Savings,1,$secondId
                2022-11-01,monthly,,True,13.00,Debit,,,,,Bill,Food,1,$thirdId
                
            """.trimIndent()
        file().writeText(initialContents)

        database.read()
        database.flush()

        file().readText() shouldBe initialContents
    }

    test("can update a standing order") {
        val id = UUID.randomUUID()
        val otherStandingOrder = Factory(id = UUID.randomUUID()).standingOrder()
        database.data = mutableListOf(Factory(id = id).standingOrder(), otherStandingOrder)

        val updated = Factory(id = id, date = Date(LocalDate.of(2020, 2, 1))).standingOrder()

        database.update(updated)

        database.data
            .shouldHaveSize(2)
            .shouldContain(otherStandingOrder)
            .shouldContain(updated)
    }
})

private const val tmp: String = "tmp"
private const val filePath = "standing_orders.csv"
private fun file() = File("$tmp/$filePath")