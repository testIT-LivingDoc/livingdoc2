package org.livingdoc.repositories.format

import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.file.DocumentFile
import java.util.*

internal object DocumentFormatManager {

    private val documentFormats: List<DocumentFormat> = ServiceLoader.load(DocumentFormat::class.java).toList()

    /**
     *
     * @throws DocumentFormatNotFoundException
     */
    fun getFormat(file: DocumentFile): DocumentFormat {
        val extension = file.extension()
        return documentFormats
                .firstOrNull { it.canHandle(extension) }
                ?: throw DocumentFormatNotFoundException(extension)
    }
}