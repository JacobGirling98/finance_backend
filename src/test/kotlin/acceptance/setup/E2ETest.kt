package acceptance.setup

import config.FinanceServer
import config.properties
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import java.io.File
import kotlin.time.Duration

abstract class E2ETest(init: E2ETest.() -> Unit) : FunSpec() {

    private val port = 5000

    private var setupSuccessful = true

    private lateinit var server: FinanceServer

    val client = ClientForTest(port)

    init {
        beforeSpec {
            try {
                val profile = System.getenv("PROFILE") ?: null

                if (profile?.contains("test") != true) {
                    setupSuccessful = false
                } else {
                    createFiles()

                    server = FinanceServer(port)

                    server.start()
                }
            } catch (e: Exception) {
                setupSuccessful = false
                println(e.message)
            }
        }

        afterSpec {
            if (setupSuccessful) {
                server.stop()
                testFileDirectory.deleteRecursively()
            }
        }

        class MySetupExtension : TestCaseExtension {
            override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
                if (!setupSuccessful && testCase.type == TestType.Test) {
                    return TestResult.Failure(
                        Duration.ZERO,
                        AssertionError("Test profile must be enabled to run E2E tests")
                    )
                }
                return execute(testCase)
            }
        }

        extension(MySetupExtension())

        init()
    }
}

private val testFileDirectory = File(properties.dataLocation)

private val singleValueFiles = listOf(
    File("${properties.dataLocation}/accounts.csv"),
    File("${properties.dataLocation}/categories.csv"),
    File("${properties.dataLocation}/income_sources.csv"),
    File("${properties.dataLocation}/payees.csv"),
    File("${properties.dataLocation}/logins.csv")
)

private val descriptionMappingFile = File("${properties.dataLocation}/description_mappings.csv")

private val transactionFile = File("${properties.dataLocation}/transactions.csv")

private val standingOrderFile = File("${properties.dataLocation}/standing_orders.csv")

private val reminderFile = File("${properties.dataLocation}/reminders.csv")

private val files: List<File> =
    listOf(descriptionMappingFile, transactionFile, standingOrderFile, reminderFile, *singleValueFiles.toTypedArray())

private fun createFiles() {
    testFileDirectory.mkdir()

    files.forEach { it.createNewFile() }

    singleValueFiles.forEach { it.writeText("id,value") }

    descriptionMappingFile.writeText("id,full_description,short_description")

    transactionFile.writeText("id,date,outgoing,value,transaction_type,outbound_account,inbound_account,destination,source,description,category,quantity,added_by")

    standingOrderFile.writeText("id,next_date,frequency_quantity,frequency_unit,category,value,description,type,outgoing,quantity,recipient,inbound,outbound,source")

    reminderFile.writeText("id,next_reminder,frequency_unit,frequency_quantity,description")
}