package unit.dao.csv

import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import dao.asEntity
import dao.csv.TransactionCsvDatabase
import unit.fixtures.*
import java.io.File
import java.time.LocalDate
import java.util.*
import kotlin.time.Duration

class TransactionCsvDatabaseTest : FunSpec({

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
        val incomeUUID = UUID.randomUUID()

        file.writeText(
            """
            id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
            $debitUUID,2020-01-01,true,1,Debit,,,,,Bananas,Food,1
            $creditUUID,2020-01-02,true,1,Credit,,,,,Bananas,Food,1
            $bankTransferUUID,2020-01-03,true,1,Bank Transfer,,,Parents,,Bananas,Food,1
            $personalTransferUUID,2020-01-04,false,1,Personal Transfer,Current,Savings,,,Bananas,Food,1
            $incomeUUID,2020-01-05,false,1,Income,,,,Work,Bananas,Food,1
        """.trimIndent()
        )

        database().selectAll() shouldContainExactlyInAnyOrder listOf(
            Transaction(
                date = Date(LocalDate.of(2020, 1, 1)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.DEBIT,
                outgoing = Outgoing(true)
            ).asEntity(debitUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 2)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.CREDIT,
                outgoing = Outgoing(true)
            ).asEntity(creditUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 3)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.BANK_TRANSFER,
                outgoing = Outgoing(true),
                recipient = Recipient("Parents")
            ).asEntity(bankTransferUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 4)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.PERSONAL_TRANSFER,
                outgoing = Outgoing(false),
                inbound = Inbound("Savings"),
                outbound = Outbound("Current")
            ).asEntity(personalTransferUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 5)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.INCOME,
                outgoing = Outgoing(false),
                source = Source("Work")
            ).asEntity(incomeUUID)
        )
    }

    test("can flush a standing order to a file") {
        file.writeText("id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity\n")
        val database = database()
        val debitId = database.save(aDebitTransaction())
        val creditId = database.save(aCreditTransaction())
        val bankTransferId = database.save(aBankTransferTransaction())
        val personalTransferId = database.save(aPersonalTransferTransaction())
        val incomeId = database.save(aWagesIncome())

        database.flush()

        file.readText() shouldBe """
            id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
            $debitId,2020-01-01,true,1,Debit,,,,,Bananas,Food,1
            $creditId,2020-01-01,true,1,Credit,,,,,Bananas,Food,1
            $bankTransferId,2020-01-01,true,1,Bank Transfer,,,Parents,,Bananas,Food,1
            $personalTransferId,2020-01-01,false,1,Personal Transfer,outbound,inbound,,,Bananas,Food,1
            $incomeId,2020-01-01,false,1,Income,,,,Work,Bananas,Wages,1
        """.trimIndent()

    }

})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = TransactionCsvDatabase(Duration.ZERO, FILE_LOCATION)