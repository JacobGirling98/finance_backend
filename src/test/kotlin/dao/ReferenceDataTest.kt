package dao

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.hasElement
import config.CustomJackson
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class ReferenceDataTest {

    private val tmp: String = "tmp"
    private val filePath = "description_mappings.txt"
    private val referenceData = ReferenceData(tmp)

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
    fun `can add description mapping`() {
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

        assertThat(referenceData.descriptions, hasElement(newDescription))
        assertThat(
            referenceData.readDescriptions(),
            hasElement(newDescription)
                .and(
                    hasElement(
                        existingDescription
                    )
                )
        )
    }

    @Test
    fun `can add multiple description mappings`() {
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

        assertThat(referenceData.descriptions, hasElement(newDescription).and(hasElement(newerDescription)))
        assertThat(
            referenceData.readDescriptions(),
            hasElement(newDescription)
                .and(
                    hasElement(
                        existingDescription
                    )
                )
                .and(
                    hasElement(
                        newerDescription
                    )
                )
        )
    }

    private fun file() = File("$tmp/$filePath")
}