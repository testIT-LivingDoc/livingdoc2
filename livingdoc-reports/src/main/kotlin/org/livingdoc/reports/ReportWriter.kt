package org.livingdoc.reports

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class ReportWriter(
    private val outputDir: String = REPORT_OUTPUT_PATH,
    private val fileExtension: String
) {

    private companion object {
        const val REPORT_OUTPUT_PATH = "build/livingdoc/reports/"
        const val REPORT_OUTPUT_FILENAME = "result"
    }

    /**
     * Write the [textToWrite] as report to the configured location. The reports filename will contain the [reportName]
     * and end with the [fileExtension].
     */
    fun writeToFile(textToWrite: String, reportName: String = REPORT_OUTPUT_FILENAME): Path {
        val path = Paths.get(outputDir)
        Files.createDirectories(path)

        val file = path.resolve("$reportName.$fileExtension")
        Files.write(file, textToWrite.toByteArray(), StandardOpenOption.CREATE_NEW)
        return file
    }
}
