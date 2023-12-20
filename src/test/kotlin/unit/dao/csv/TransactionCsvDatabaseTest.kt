package unit.dao.csv

import dao.asEntity
import dao.csv.TransactionCsvDatabase
import domain.*
import domain.Date
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import unit.fixtures.aBankTransferTransaction
import unit.fixtures.aCreditTransaction
import unit.fixtures.aDebitTransaction
import unit.fixtures.aPersonalTransferTransaction
import unit.fixtures.aWagesIncome
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
            id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,added_by
            $debitUUID,2020-01-01,true,1,Debit,,,,,Bananas,Food,1,Jacob
            $creditUUID,2020-01-02,true,1,Credit,,,,,Bananas,Food,1,Jake
            $bankTransferUUID,2020-01-03,true,1,Bank Transfer,,,Parents,,Bananas,Food,1,Jack
            $personalTransferUUID,2020-01-04,false,1,Personal Transfer,Current,Savings,,,Bananas,Food,1,Jacob
            $incomeUUID,2020-01-05,false,1,Income,,,,Work,Bananas,Food,1,Jacob
            """.trimIndent()
        )

        database().selectAll() shouldContainExactlyInAnyOrder listOf(
            Transaction(
                date = Date(LocalDate.of(2020, 1, 1)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.DEBIT,
                outgoing = Outgoing(true),
                addedBy = AddedBy("Jacob")
            ).asEntity(debitUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 2)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.CREDIT,
                outgoing = Outgoing(true),
                addedBy = AddedBy("Jake")
            ).asEntity(creditUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 3)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.BANK_TRANSFER,
                outgoing = Outgoing(true),
                recipient = Recipient("Parents"),
                addedBy = AddedBy("Jack")
            ).asEntity(bankTransferUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 4)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.PERSONAL_TRANSFER,
                outgoing = Outgoing(false),
                inbound = Inbound("Savings"),
                outbound = Outbound("Current"),
                addedBy = AddedBy("Jacob")
            ).asEntity(personalTransferUUID),
            Transaction(
                date = Date(LocalDate.of(2020, 1, 5)),
                category = Category("Food"),
                value = Value.of(1.0),
                description = Description("Bananas"),
                type = TransactionType.INCOME,
                outgoing = Outgoing(false),
                source = Source("Work"),
                addedBy = AddedBy("Jacob")
            ).asEntity(incomeUUID)
        )
    }

    test("can flush a standing order to a file") {
        file.writeText("id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,added_by\n")
        val database = database()
        val debitId = database.save(aDebitTransaction())
        val creditId = database.save(aCreditTransaction())
        val bankTransferId = database.save(aBankTransferTransaction())
        val personalTransferId = database.save(aPersonalTransferTransaction())
        val incomeId = database.save(aWagesIncome())

        database.flush()

        file.readText() shouldBe """
            id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,added_by
            $debitId,2020-01-01,true,1,Debit,,,,,Bananas,Food,1,Jacob
            $creditId,2020-01-01,true,1,Credit,,,,,Bananas,Food,1,Jacob
            $bankTransferId,2020-01-01,true,1,Bank Transfer,,,Parents,,Bananas,Food,1,Jacob
            $personalTransferId,2020-01-01,false,1,Personal Transfer,outbound,inbound,,,Bananas,Food,1,Jacob
            $incomeId,2020-01-01,false,1,Income,,,,Work,Bananas,Wages,1,Jacob
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = TransactionCsvDatabase(Duration.ZERO, FILE_LOCATION)
