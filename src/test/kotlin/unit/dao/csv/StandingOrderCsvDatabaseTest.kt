package unit.dao.csv

import dao.asEntity
import dao.csv.StandingOrderCsvDatabase
import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import unit.fixtures.aBankTransferStandingOrder
import unit.fixtures.aCreditStandingOrder
import unit.fixtures.aDebitStandingOrder
import unit.fixtures.aPersonalTransferStandingOrder
import java.io.File
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration

class StandingOrderCsvDatabaseTest : FunSpec({

    beforeEach {
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    afterEach { if (file.exists()) file.delete() }

    test("can read from a file") {
        val debitUUID = UUID.randomUUID()
        val creditUUID = UUID.randomUUID()
        val bankTransferUUID = UUID.randomUUID()
        val personalTransferUUID = UUID.randomUUID()

        file.writeText(
            """
            id,next_date,frequency_quantity,frequency_unit,category,value,description,type,outgoing,quantity,recipient,inbound,outbound
            $debitUUID,2020-01-01,1,monthly,Food,1.5,Bananas,Debit,true,1,,,
            $creditUUID,2020-01-02,1,weekly,Food,1,Bananas,Credit,true,1,,,
            $bankTransferUUID,2020-01-03,1,monthly,Food,1,Bananas,Bank Transfer,true,1,Parents,,
            $personalTransferUUID,2020-01-04,2,weekly,Food,1,Bananas,Personal Transfer,false,1,,Savings,Current
        """.trimIndent()
        )

        database().selectAll() shouldContainExactlyInAnyOrder listOf(
            StandingOrder(
                Date(LocalDate.of(2020, 1, 1)),
                FrequencyQuantity(1),
                Frequency.MONTHLY,
                Category("Food"),
                Value.of(1.5),
                Description("Bananas"),
                TransactionType.DEBIT,
                Outgoing(true)
            ).asEntity(debitUUID),
            StandingOrder(
                Date(LocalDate.of(2020, 1, 2)),
                FrequencyQuantity(1),
                Frequency.WEEKLY,
                Category("Food"),
                Value.of(1.0),
                Description("Bananas"),
                TransactionType.CREDIT,
                Outgoing(true)
            ).asEntity(creditUUID),
            StandingOrder(
                Date(LocalDate.of(2020, 1, 3)),
                FrequencyQuantity(1),
                Frequency.MONTHLY,
                Category("Food"),
                Value.of(1.0),
                Description("Bananas"),
                TransactionType.BANK_TRANSFER,
                Outgoing(true),
                recipient = Recipient("Parents")
            ).asEntity(bankTransferUUID),
            StandingOrder(
                Date(LocalDate.of(2020, 1, 4)),
                FrequencyQuantity(2),
                Frequency.WEEKLY,
                Category("Food"),
                Value.of(1.0),
                Description("Bananas"),
                TransactionType.PERSONAL_TRANSFER,
                Outgoing(false),
                inbound = Inbound("Savings"),
                outbound = Outbound("Current")
            ).asEntity(personalTransferUUID)
        )
    }

    test("can flush a standing order to a file") {
        file.writeText("id,next_date,frequency_quantity,frequency_unit,category,value,description,type,outgoing,quantity,recipient,inbound,outbound\n")
        val database = database()
        val debitId = database.save(aDebitStandingOrder())
        val creditId = database.save(aCreditStandingOrder())
        val bankTransferId = database.save(aBankTransferStandingOrder())
        val personalTransferId = database.save(aPersonalTransferStandingOrder())

        database.flush()

        file.readText() shouldBe """
            id,next_date,frequency_quantity,frequency_unit,category,value,description,type,outgoing,quantity,recipient,inbound,outbound
            $debitId,2020-01-01,1,monthly,Food,1,Bananas,Debit,true,1,,,
            $creditId,2020-01-01,1,monthly,Food,1,Bananas,Credit,true,1,,,
            $bankTransferId,2020-01-01,1,monthly,Food,1,Bananas,Bank Transfer,true,1,Parents,,
            $personalTransferId,2020-01-01,1,monthly,Food,1,Bananas,Personal Transfer,false,1,,inbound,outbound
        """.trimIndent()

    }

})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = StandingOrderCsvDatabase(Duration.ZERO, FILE_LOCATION)