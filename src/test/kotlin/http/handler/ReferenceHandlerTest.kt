package http.handler

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.hasElement
import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test

class ReferenceHandlerTest {

    @Test
    fun `can post descriptions`() {
        var arguments = listOf<DescriptionMapping>()
        val handler = postDescriptionsHandler { arguments = it }

        val response = handler(
            Request(POST, "/").body(
                """
                [
                    {
                        "shortDescription": "Milk",
                        "fullDescription": "4 pint milk"
                    },
                    {
                        "shortDescription": "Caramel",
                        "fullDescription": "Dairy milk caramel"
                    }
                ]
                """.trimIndent()
            )
        )
        assertThat(response, hasStatus(OK))
        assertThat(
            arguments, hasElement(
                DescriptionMapping(
                    FullDescription("4 pint milk"),
                    ShortDescription("Milk")
                )
            ) and hasElement(
                DescriptionMapping(
                    FullDescription("Dairy milk caramel"),
                    ShortDescription("Caramel")
                )
            )
        )
    }
}