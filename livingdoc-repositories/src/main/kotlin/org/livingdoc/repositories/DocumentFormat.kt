package org.livingdoc.repositories

import java.io.InputStream

interface DocumentFormat {

    fun parse(stream: InputStream): Document

}