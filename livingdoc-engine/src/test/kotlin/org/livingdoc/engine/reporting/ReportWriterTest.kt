package org.livingdoc.engine.reporting

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File


internal class ReportWriterTest {

    val cut = ReportWriter()

    @AfterEach
    fun cleanUp() {
        File("build/livingdoc").deleteRecursively()
    }

    @Test
    fun `report file is correctly written to build directory`() {
        val textToWrite = """
            <html>
                <body>
                    <p>TEXT</p>
                </body>
            </html>
        """.trimIndent()

        var listFiles = File("build/livingdoc/reports").listFiles()
        assertThat(listFiles).isNullOrEmpty()

        cut.writeToFile(textToWrite)

        listFiles = File("build/livingdoc/reports").listFiles()
        assertThat(listFiles).hasSize(1)
        assertThat(listFiles[0].name).startsWith("result").endsWith(".html")
        assertThat(listFiles[0].readText()).isEqualToIgnoringWhitespace(textToWrite)
    }
}
