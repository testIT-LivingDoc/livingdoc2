package org.livingdoc.repositories.file

import org.livingdoc.repositories.DocumentNotFoundException
import java.nio.file.Path

class FileDocumentNotFoundException(id: String, path: Path)
    : DocumentNotFoundException("Could not find document with ID [$id] in path [$path]!")