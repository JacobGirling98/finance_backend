package dao

import domain.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate

class TransactionsDatabaseTest : FunSpec({

    val table = TransactionsDatabase(tmp)

    beforeEach {
        File(tmp).deleteRecursively()
        File(tmp).mkdirs()
    }

    afterEach {
        File(tmp).deleteRecursively()
    }

    test("can read from csv of multiple credit transactions") {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
                2020-10-30,True,19.75,Credit,,,,,Row 2,Tech,2
                
            """.trimIndent()
        )

        table.read()

        table.data shouldBe listOf(
            Transaction(
                Date(LocalDate.of(2020, 10, 29)),
                Category("Tech"),
                Value(BigDecimal("45.50")),
                Description("Row 1"),
                TransactionType.CREDIT,
                Outgoing(true),
                quantity = Quantity(1),
            ),
            Transaction(
                Date(LocalDate.of(2020, 10, 30)),
                Category("Tech"),
                Value(BigDecimal("19.75")),
                Description("Row 2"),
                TransactionType.CREDIT,
                Outgoing(true),
                quantity = Quantity(2),
            )
        )
    }

    test("can read from a csv of debit transactions") {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Debit,,,,,Row 1,Tech,1
                
            """.trimIndent()
        )

        table.read()

        table.data shouldHaveSingleElement Transaction(
            Date(LocalDate.of(2020, 10, 29)),
            Category("Tech"),
            Value(BigDecimal("45.50")),
            Description("Row 1"),
            TransactionType.DEBIT,
            Outgoing(true),
            quantity = Quantity(1),
        )
    }

    test("can read from a csv of bank transfer transactions") {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-16,True,123.00,Bank Transfer,,,Friend,,Flat Rent,Rent,1
                
            """.trimIndent()
        )

        table.read()

        table.data shouldHaveSingleElement Transaction(
            Date(LocalDate.of(2020, 10, 16)),
            Category("Rent"),
            Value(BigDecimal("123.00")),
            Description("Flat Rent"),
            TransactionType.BANK_TRANSFER,
            Outgoing(true),
            quantity = Quantity(1),
            recipient = Recipient("Friend")
        )

    }

    test("can read from a csv of personal transfer transactions") {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-16,False,100.00,Personal Transfer,Current,Saver Account,,,Savings,Savings,1
                
            """.trimIndent()
        )

        table.read()

        table.data shouldHaveSingleElement Transaction(
            Date(LocalDate.of(2020, 10, 16)),
            Category("Savings"),
            Value(BigDecimal("100.00")),
            Description("Savings"),
            TransactionType.PERSONAL_TRANSFER,
            Outgoing(false),
            quantity = Quantity(1),
            outbound = Outbound("Current"),
            inbound = Inbound("Saver Account")
        )

    }

    test("flushing to a file without changes does not change file") {
        val initialContents = """
            date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
            2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
            2020-10-29,True,45.50,Debit,,,,,Row 1,Tech,1
            2020-10-16,True,123.00,Bank Transfer,,,Friend,,Flat Rent,Rent,1
            2020-10-16,False,100.00,Personal Transfer,Current,Saver Account,,,Savings,Savings,1
            
        """.trimIndent()
        file().writeText(initialContents)

        table.read()
        table.flush()

        file().readText() shouldBe initialContents
    }

    test("flushing to a file with changes will change the file") {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
                2020-10-30,True,19.75,Credit,,,,,Row 2,Tech,2
                
            """.trimIndent()
        )

        table.data = mutableListOf(
            Transaction(
                Date(LocalDate.of(2020, 1, 1)),
                Category("Coffee"),
                Value(BigDecimal("3.00")),
                Description("Latte"),
                TransactionType.CREDIT,
                Outgoing(true),
                Quantity(1)
            )
        )
        table.flush()

        file().readText() shouldBe """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-01-01,True,3.00,Credit,,,,,Latte,Coffee,1
                
            """.trimIndent()
    }

    test("can save a transaction") {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
                2020-10-30,True,19.75,Credit,,,,,Row 2,Tech,2
                
            """.trimIndent()
        )
        table.read()

        table.save(
            Transaction(
                Date(LocalDate.of(2020, 1, 1)),
                Category("Coffee"),
                Value(BigDecimal("3.00")),
                Description("Latte"),
                TransactionType.CREDIT,
                Outgoing(true),
                Quantity(1)
            )
        )

        file().readText() shouldBe """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
                2020-10-30,True,19.75,Credit,,,,,Row 2,Tech,2
                2020-01-01,True,3.00,Credit,,,,,Latte,Coffee,1
                
            """.trimIndent()
    }

    test("can save multiple transactions") {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
                2020-10-30,True,19.75,Credit,,,,,Row 2,Tech,2
                
            """.trimIndent()
        )
        table.read()

        table.save(
            listOf(
                Transaction(
                    Date(LocalDate.of(2020, 1, 1)),
                    Category("Coffee"),
                    Value(BigDecimal("3.00")),
                    Description("Latte"),
                    TransactionType.CREDIT,
                    Outgoing(true),
                    Quantity(1)
                ),
                Transaction(
                    Date(LocalDate.of(2020, 1, 2)),
                    Category("Food"),
                    Value(BigDecimal("1.00")),
                    Description("Banana"),
                    TransactionType.DEBIT,
                    Outgoing(true),
                    Quantity(5)
                )
            )
        )

        file().readText() shouldBe """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
                2020-10-30,True,19.75,Credit,,,,,Row 2,Tech,2
                2020-01-01,True,3.00,Credit,,,,,Latte,Coffee,1
                2020-01-02,True,1.00,Debit,,,,,Banana,Food,5
                
            """.trimIndent()
    }
})

private const val tmp: String = "tmp"
private const val filePath = "data.csv"
private fun file() = File("$tmp/$filePath")