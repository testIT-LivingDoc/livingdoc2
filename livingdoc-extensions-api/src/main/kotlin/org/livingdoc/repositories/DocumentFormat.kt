package org.livingdoc.repositories

import java.io.InputStream

/**
 * A DocumentFormat describes the format in which data is stored inside a [DocumentRepository].
 *
 * @see DocumentRepository
 */
interface DocumentFormat {
    /**
     * canHandle checks whether this DocumentFormat can handle a file with the specified extension.
     *
     * @param fileExtension the extension of the potential input file
     * @return true, if this DocumentFormat can handle the
     */
    fun canHandle(fileExtension: String): Boolean

    /**
     * parse creates a [Document] from the input
     *
     * @param stream an [InputStream] representing the document to parse
     * @returns the parsed [Document]
     */
    fun parse(stream: InputStream): Document
}
