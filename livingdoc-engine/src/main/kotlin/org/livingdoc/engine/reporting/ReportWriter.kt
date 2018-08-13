package org.livingdoc.engine.reporting

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReportWriter {

    private val REPORT_OUTPUT_PATH = "build/livingdoc/reports/"
    private val REPORT_OUTPUT_FILENAME = "result"
    private val REPORT_OUTPUT_EXTENSION = ".html"
    private val REPORT_OUTPUT_DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss"

    fun writeToFile(textToWrite: String) {
        File(REPORT_OUTPUT_PATH).mkdirs()
        val dateAsString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(REPORT_OUTPUT_DATE_FORMAT))
        val reportFile = File(REPORT_OUTPUT_PATH + REPORT_OUTPUT_FILENAME + dateAsString + REPORT_OUTPUT_EXTENSION)
        reportFile.writeText(textToWrite)
    }
}