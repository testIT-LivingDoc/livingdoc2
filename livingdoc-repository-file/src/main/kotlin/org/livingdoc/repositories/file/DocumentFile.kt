package org.livingdoc.repositories.file

import org.livingdoc.repositories.format.DocumentFormatManager
import java.io.File

open class DocumentFile(private val file: File) {
    open fun extension() = file.extension
    open fun stream() = file.inputStream()
    open fun format() = DocumentFormatManager.getFormat(this)
}