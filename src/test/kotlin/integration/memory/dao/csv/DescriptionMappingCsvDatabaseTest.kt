package integration.memory.dao.csv

import dao.csv.DescriptionMappingCsvDatabase
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import helpers.matchers.shouldContainDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.util.*
import kotlin.time.Duration

class DescriptionMappingCsvDatabaseTest : FunSpec({

    beforeEach {
        if (file.exists()) file.delete()
        file.createNewFile()
    }

    afterEach { if (file.exists()) file.delete() }

    test("can read from a file") {
        val id = UUID.randomUUID()

        file.writeText(
            """
            id,full_description,short_description
            $id,lots of bananas,bananas
            """.trimIndent()
        )

        database().selectAll() shouldContainDomain DescriptionMapping(
            FullDescription("lots of bananas"),
            ShortDescription("bananas")
        )
    }

    test("can flush a mapping to a file") {
        file.writeText("id,full_description,short_description")
        val database = database()
        val id = database.save(DescriptionMapping(FullDescription("lots of bananas"), ShortDescription("bananas")))

        database.flush()

        file.readText() shouldBe """
            id,full_description,short_description
            $id,lots of bananas,bananas
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = DescriptionMappingCsvDatabase(Duration.ZERO, FILE_LOCATION)
