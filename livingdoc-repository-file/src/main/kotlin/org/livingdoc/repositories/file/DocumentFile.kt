package org.livingdoc.repositories.file

import java.io.File

open class DocumentFile(private val file: File) {
    open fun extension() = file.extension
    open fun stream() = file.inputStream()
}
