package org.livingdoc.repositories.file

import java.io.File
import org.livingdoc.repositories.Document

/**
 * A DocumentFile wraps a [File] that contains a [Document]
 *
 * @see Document
 * @see File
 */
open class DocumentFile(private val file: File) {
    open fun extension() = file.extension
    open fun stream() = file.inputStream()
}
