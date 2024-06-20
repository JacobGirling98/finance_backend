package unit.http.handler

import http.handler.handlerFor
import http.handler.unitHandlerFor
import http.lense.biDiBodyLens
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.kotest.shouldHaveBody
import org.http4k.kotest.shouldHaveStatus

class HandlersTest : FunSpec({


    test("unit handler") {
        val unitFn = mockk<() -> Unit>()
        every { unitFn() } just runs
        val handler = unitHandlerFor(unitFn)

        val response = handler(Request(GET, ""))

        response shouldHaveStatus Status.NO_CONTENT
        verify { unitFn() }
    }

    test("handlerFor") {
        val fn = mockk<(String) -> String>()
        every { fn(any()) } returns "fn result"
        val requestExtractor = mockk<(Request) -> String>()
        every { requestExtractor(any()) } returns "extractor result"
        val lens = biDiBodyLens<String>()
        val request = Request(GET, "")
        val handler = handlerFor(fn, OK, requestExtractor, lens)

        val response = handler(request)

        response shouldHaveStatus OK
        response shouldHaveBody """"fn result""""
        verify { requestExtractor(request) }
        verify { fn("extractor result") }
    }

})