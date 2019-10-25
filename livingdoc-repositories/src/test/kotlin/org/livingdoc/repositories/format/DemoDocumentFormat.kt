package org.livingdoc.repositories.format

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentFormat
import java.io.InputStream

class DemoDocumentFormat : DocumentFormat {
    private val supportedFileExtensions = setOf("df", "dfm")

    override fun canHandle(fileExtension: String): Boolean {
        return supportedFileExtensions.contains(fileExtension.toLowerCase())
    }

    override fun parse(stream: InputStream): Document {
        return Document(emptyList())
    }
}
