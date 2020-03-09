package org.livingdoc.repositories.git

import org.eclipse.jgit.lib.Constants
import java.io.File

/**
 * A GitDocumentIdentifier identifies a document stored in a git repository by path and version
 */
internal class GitDocumentIdentifier(val path: String, private val version: String? = null) {
    val format: String
        get() = File(path).extension

    val revision
        get() = version ?: Constants.HEAD

    companion object {
        /**
         * parse extracts a document path and optional version from a documentIdentifier
         */
        fun parse(documentIdentifier: String): GitDocumentIdentifier {
            val parts = documentIdentifier.split('@')

            if (2 < parts.size) {
                throw InvalidDocumentIdentifierException(documentIdentifier)
            }

            val path = parts[0]
            val version = parts.getOrNull(1).takeUnless { it.isNullOrBlank() }

            return GitDocumentIdentifier(path, version)
        }
    }
}

class InvalidDocumentIdentifierException(documentIdentifier: String) :
    Exception("Invalid document identifier: $documentIdentifier")
