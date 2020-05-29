package org.livingdoc.reports

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

internal class ReportWriterTest {

    @Test
    fun `report file is correctly written to directory`(@TempDir tempDir: Path) {
        val textToWrite = """
            <html>
                <body>
                    <p>TEXT</p>
                </body>
            </html>
        """.trimIndent()

        val fileExtension = "test"
        val fileName = "testFileName"
        val reportsDir = tempDir.resolve("reports")
        Files.createDirectory(reportsDir)
        val cut = ReportWriter(reportsDir.toString(), fileExtension)

        cut.writeToFile(textToWrite, fileName)

        Files.list(reportsDir).use {
            val listFiles = it.toList()
            assertThat(listFiles).hasSize(1)
            assertThat(listFiles[0].fileName.toString()).startsWith(fileName).endsWith(".$fileExtension")
            assertThat(String(Files.readAllBytes(listFiles[0]))).isEqualToIgnoringWhitespace(textToWrite)
        }
    }

    @Test
    fun `Multiple reports can be written`(@TempDir tempDir: Path) {
        val cut = ReportWriter(tempDir.toString(), "test")

        cut.writeToFile("test1", "report1")
        cut.writeToFile("test2", "report2")
        Files.list(tempDir).use {
            val listFiles = it.toList()
            assertThat(listFiles).hasSize(2)
        }
    }

    @Test
    fun `Throw exception if file already exists`(@TempDir tempDir: Path) {
        val cut = ReportWriter(tempDir.toString(), "test")

        cut.writeToFile("test1", "testFileName")
        assertThrows<FileAlreadyExistsException> { cut.writeToFile("test2", "testFileName") }
    }
}
