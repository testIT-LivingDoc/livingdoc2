package org.livingdoc.repositories.file

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class FileRepositoryTest {

    val cut = FileRepository("", FileRepositoryConfig())

    @Test fun `exception is thrown if document could not be found`() {
        assertThrows<FileDocumentNotFoundException> {
            cut.getDocument("foo-bar.md")
        }
    }
}
