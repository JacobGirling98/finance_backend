package integration.http.google

import config.properties
import http.google.GoogleDrive
import http.google.MimeType
import http.google.Synchronisable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.time.LocalDateTime

class GoogleDriveTest : FunSpec({

    val drive = GoogleDrive(properties.google.credentialsFile)
    val time = LocalDateTime.now()
    val dir = File("tmp")
    val file = File(dir, "tmp.txt")

    beforeTest {
        dir.mkdirs()
        file.writeText(time.toString())
    }

    afterTest { dir.deleteRecursively() }

    test("can get contents of a remote file") {
        val fileName = "testfile.txt"
        val expectedContents = """
            Hello there
            Test
        """.trimIndent()

        drive.readText(fileName) shouldBe expectedContents
    }

    test("error thrown if file not found") {
        shouldThrow<RuntimeException> { drive.readText("randomfile") }
    }

    test("can update a file") {
        val synchronisable = object : Synchronisable {
            override val mimeType = MimeType.TEXT_PLAIN
            override fun latestFile() = file
            override fun overwrite(data: String) {}
        }

        drive.updateFile(synchronisable)

        drive.readText(file.name) shouldBe time.toString()
    }
})
