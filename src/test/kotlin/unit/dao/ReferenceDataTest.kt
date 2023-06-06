package unit.dao

import config.CustomJackson
import dao.ReferenceData
import dao.writeLine
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import java.io.File

class ReferenceDataTest : FunSpec({


    val referenceData = ReferenceData(tmp)

    beforeEach {
        File(tmp).deleteRecursively()
        File(tmp).mkdirs()
    }

    afterEach {
        File(tmp).deleteRecursively()
    }

    test("can add description mapping") {
        val existingDescription = DescriptionMapping(
            FullDescription("Tomato Soup"),
            ShortDescription("Soup")
        )
        file().writeLine(CustomJackson.mapper.writeValueAsString(existingDescription))
        val newDescription = DescriptionMapping(
            FullDescription("4 Pint Milk"),
            ShortDescription("Milk")
        )

        referenceData.save(newDescription)

        referenceData.descriptions.shouldContain(newDescription)
        referenceData.readDescriptions()
            .shouldContain(newDescription)
            .shouldContain(existingDescription)
    }

    test("can add multiple description mappings") {
        val existingDescription = DescriptionMapping(
            FullDescription("Tomato Soup"),
            ShortDescription("Soup")
        )
        file().writeLine(CustomJackson.mapper.writeValueAsString(existingDescription))
        val newDescription = DescriptionMapping(
            FullDescription("4 Pint Milk"),
            ShortDescription("Milk")
        )
        val newerDescription = DescriptionMapping(
            FullDescription("Cadbury Caramel"),
            ShortDescription("Caramel")
        )

        referenceData.save(listOf(newDescription, newerDescription))

        referenceData.descriptions
            .shouldContain(newDescription)
            .shouldContain(newerDescription)
        referenceData.readDescriptions()
            .shouldContain(newDescription)
            .shouldContain(existingDescription)
            .shouldContain(newerDescription)
    }
})

private const val tmp = "tmp"
private const val filePath = "description_mappings.txt"
private fun file() = File("$tmp/$filePath")