package org.livingdoc.repositories.format

import org.livingdoc.repositories.DocumentFormat
import java.util.*

/**
 * A Manager for Document Formats. The Manager uses the ServiceLoader API to find the Formats.
 */
object DocumentFormatManager {

    private val documentFormats: List<DocumentFormat> = ServiceLoader.load(DocumentFormat::class.java).toList()

    /**
     * Get the Format for a file extension
     *
     * @throws DocumentFormatNotFoundException
     */
    fun getFormat(extension: String): DocumentFormat {
        return documentFormats
            .firstOrNull { it.canHandle(extension) }
            ?: throw DocumentFormatNotFoundException(extension)
    }
}
