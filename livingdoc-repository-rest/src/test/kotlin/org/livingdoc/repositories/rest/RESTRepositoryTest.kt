package org.livingdoc.repositories.rest

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RESTRepositoryTest {

    val cut = RESTRepository("test", RESTRepositoryConfig())

    @Test
    fun `exception is thrown if document could not be found`() {
        assertThrows<RESTDocumentNotFoundException> {
            cut.getDocument("foo-bar.html")
        }
    }
}
