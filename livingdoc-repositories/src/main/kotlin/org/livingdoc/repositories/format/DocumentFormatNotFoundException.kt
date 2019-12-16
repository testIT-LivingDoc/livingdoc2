package org.livingdoc.repositories.format

import org.livingdoc.repositories.DocumentFormat

/**
 * DocumentFormatNotFoundException is thrown when the [DocumentFormatManager] can not find a [DocumentFormat] for a
 * given file extension.
 *
 * @see DocumentFormat
 * @see DocumentFormatManager
 */
class DocumentFormatNotFoundException(fileExtension: String) :
    RuntimeException(fileExtension)
