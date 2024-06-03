package integration.memory.dao.csv

import dao.AuditableEntity
import dao.Entity
import dao.csv.DescriptionMappingCsvDatabase
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import helpers.fixtures.lastModified
import helpers.fixtures.lastModifiedString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
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
            id,last_modified,full_description,short_description
            $id,$lastModifiedString,lots of bananas,bananas
            """.trimIndent()
        )

        database().selectAll() shouldContain AuditableEntity(
            id,
            DescriptionMapping(
                FullDescription("lots of bananas"),
                ShortDescription("bananas")
            ),
            lastModified
        )
    }

    test("can flush a mapping to a file") {
        file.writeText("id,last_modified,full_description,short_description")
        val database = database()
        val id = database.save(DescriptionMapping(FullDescription("lots of bananas"), ShortDescription("bananas")))

        database.flush()

        file.readText() shouldBe """
            id,last_modified,full_description,short_description
            $id,$lastModifiedString,lots of bananas,bananas
        """.trimIndent()
    }
})

private const val FILE_LOCATION = "test.csv"
private val file = File(FILE_LOCATION)

private fun database() = DescriptionMappingCsvDatabase(Duration.ZERO, FILE_LOCATION) { lastModified }
