package dao

import domain.*
import domain.TransactionType.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

class DatabaseTest {
    private val tmp: String = "tmp"
    private val filePath = "data.csv"
    private val table = Database(tmp)


    @BeforeEach
    fun setup() {
        File(tmp).deleteRecursively()
        File(tmp).mkdirs()
    }

    @AfterEach
    fun teardown() {
        File(tmp).deleteRecursively()
    }

    @Test
    fun `can read from csv of multiple credit transactions`() {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Credit,,,,,Row 1,Tech,1
                2020-10-30,True,19.75,Credit,,,,,Row 2,Tech,2
                
            """.trimIndent()
        )

        table.read()

        assertEquals(
            listOf(
                Transaction(
                    Date(LocalDate.of(2020, 10, 29)),
                    Category("Tech"),
                    Value(BigDecimal("45.50")),
                    Description("Row 1"),
                    CREDIT,
                    true,
                    quantity = Quantity(1),
                ),
                Transaction(
                    Date(LocalDate.of(2020, 10, 30)),
                    Category("Tech"),
                    Value(BigDecimal("19.75")),
                    Description("Row 2"),
                    CREDIT,
                    true,
                    quantity = Quantity(2),
                )
            ),
            table.data
        )
    }

    @Test
    fun `can read from a csv of debit transactions`() {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-29,True,45.50,Debit,,,,,Row 1,Tech,1
                
            """.trimIndent()
        )

        table.read()

        assertEquals(
            listOf(
                Transaction(
                    Date(LocalDate.of(2020, 10, 29)),
                    Category("Tech"),
                    Value(BigDecimal("45.50")),
                    Description("Row 1"),
                    DEBIT,
                    true,
                    quantity = Quantity(1),
                ),
            ),
            table.data
        )
    }

    @Test
    fun `can read from a csv of bank transfer transactions`() {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-16,True,123.00,Bank Transfer,,,Friend,,Flat Rent,Rent,1
                
            """.trimIndent()
        )

        table.read()

        assertEquals(
            listOf(
                Transaction(
                    Date(LocalDate.of(2020, 10, 16)),
                    Category("Rent"),
                    Value(BigDecimal("123.00")),
                    Description("Flat Rent"),
                    BANK_TRANSFER,
                    true,
                    quantity = Quantity(1),
                    recipient = Recipient("Friend")
                ),
            ),
            table.data
        )
    }

    @Test
    fun `can read from a csv of personal transfer transactions`() {
        file().writeText(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-10-16,False,100.00,Personal Transfer,Current,Saver Account,,,Savings,Savings,1
                
            """.trimIndent()
        )

        table.read()

        assertEquals(
            listOf(
                Transaction(
                    Date(LocalDate.of(2020, 10, 16)),
                    Category("Savings"),
                    Value(BigDecimal("100.00")),
                    Description("Savings"),
                    PERSONAL_TRANSFER,
                    false,
                    quantity = Quantity(1),
                    outbound = Outbound("Current"),
                    inbound = Inbound("Saver Account")
                ),
            ),
            table.data
        )
    }

    @Test
    fun `flushing to a file without changes does not change file`() {
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

        assertEquals(initialContents, file().readText())
    }

    @Test
    fun `flushing to a file with changes will change the file`() {
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
                CREDIT,
                true,
                Quantity(1)
            )
        )
        table.flush()

        assertEquals(
            """
                date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity
                2020-01-01,True,3.00,Credit,,,,,Latte,Coffee,1
                
            """.trimIndent(),
            file().readText()
        )
    }

    private fun file() = File("$tmp/$filePath")
}