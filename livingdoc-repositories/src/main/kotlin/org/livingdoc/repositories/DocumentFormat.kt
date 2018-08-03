package org.livingdoc.repositories

import java.io.InputStream

interface DocumentFormat {

    fun canHandle(fileExtension: String): Boolean

    fun parse(stream: InputStream): Document

}