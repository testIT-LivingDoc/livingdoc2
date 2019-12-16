package org.livingdoc.repositories.file

import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentNotFoundException
import java.nio.file.Path

/**
 * FileDocumentNotFoundException is thrown when a [Document] could not be found within a [FileRepository].
 *
 * @see Document
 * @see DocumentNotFoundException
 * @see FileRepository
 */
class FileDocumentNotFoundException(id: String, path: Path) :
    DocumentNotFoundException("Could not find document with ID [$id] in path [$path]!")
