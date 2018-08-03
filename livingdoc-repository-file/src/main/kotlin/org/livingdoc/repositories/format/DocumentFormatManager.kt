package org.livingdoc.repositories.format

import org.livingdoc.repositories.DocumentFormat
import java.io.File
import java.util.*

internal object DocumentFormatManager {

    private val documentFormats: List<DocumentFormat> = ServiceLoader.load(DocumentFormat::class.java).toList()

    /**
     *
     * @throws DocumentFormatNotFoundException
     */
    fun getFormat(file: File): DocumentFormat {
        val extension = file.extension
        return documentFormats
                .firstOrNull { it.canHandle(extension) }
                ?: throw DocumentFormatNotFoundException(extension)
    }

}