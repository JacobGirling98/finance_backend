package unit.http.handler

import domain.DescriptionMapping
import domain.FullDescription
import domain.ShortDescription
import http.handler.postDescriptionsHandler
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveStatus
import java.util.*

class ReferenceHandlerTest : FunSpec({

    test("can post descriptions") {
        var arguments = listOf<DescriptionMapping>()
        val handler = postDescriptionsHandler { arguments = it; listOf(UUID.randomUUID()) }

        val response = handler(
            Request(Method.POST, "/").body(
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

        response shouldHaveStatus OK
        arguments
            .shouldContain(
                DescriptionMapping(
                    FullDescription("4 pint milk"),
                    ShortDescription("Milk")
                )
            ).shouldContain(
                DescriptionMapping(
                    FullDescription("Dairy milk caramel"),
                    ShortDescription("Caramel")
                )
            )
    }
})
