package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.repositories.format.DocumentFormatManager.getFormat

internal class DocumentFormatManagerTest {

    @ValueSource(strings = ["df", "dfm"])
    @ParameterizedTest fun `following file types are supported`(fileExtension: String) {
        assertThat(getFormat(fileExtension)).isNotNull()
    }

    @Test fun `exception is thrown on getting unknown format`() {
        assertThrows<DocumentFormatNotFoundException> {
            getFormat("xml")
        }
    }
}
