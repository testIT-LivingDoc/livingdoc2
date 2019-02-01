package org.livingdoc.repositories.file

import java.io.File
import java.nio.file.Paths

/**
 * Class to handle resolving file for the [FileRepository].
 */
open class FileResolver {

    /**
     * Returns an [File] for the given document root and identifier.
     */
    open fun resolveFile(documentRoot: String, documentIdentifier: String): DocumentFile {
        val file = Paths.get(documentRoot, documentIdentifier).toFile()
        when (file.exists()) {
            true -> return DocumentFile(file)
            false -> throw FileDocumentNotFoundException(documentIdentifier, file.toPath())
        }
    }
}
