package org.livingdoc.reports

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReportWriter(
    private val outputDir: String = REPORT_OUTPUT_PATH,
    private val fileExtension: String,
    private val reportDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern(REPORT_OUTPUT_DATE_FORMAT))
) {

    private companion object {
        const val REPORT_OUTPUT_PATH = "build/livingdoc/reports/"
        const val REPORT_OUTPUT_FILENAME = "result"
        const val REPORT_OUTPUT_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss"
    }

    /**
     * Write the [textToWrite] as report to the configured location. The reports filename will contain the [reportName]
     * and the [reportDate] and end with the [fileExtension].
     */
    fun writeToFile(textToWrite: String, reportName: String = REPORT_OUTPUT_FILENAME) {
        val path = Paths.get(outputDir)
        Files.createDirectories(path)

        val file = path.resolve("$reportName$reportDate.$fileExtension")
        Files.write(file, textToWrite.toByteArray(), StandardOpenOption.CREATE_NEW)
    }
}
