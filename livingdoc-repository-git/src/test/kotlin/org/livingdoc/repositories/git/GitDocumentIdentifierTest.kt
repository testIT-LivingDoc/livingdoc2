package org.livingdoc.repositories.git

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GitDocumentIdentifierTest {
    @Test
    fun `can parse simple path`() {
        val identifier = GitDocumentIdentifier.parse("Calculator.md")

        assertThat(identifier.path).isEqualTo("Calculator.md")
        assertThat(identifier.revision).isEqualTo("HEAD")
    }

    @Test
    fun `can parse path and version`() {
        val identifier = GitDocumentIdentifier.parse("Calculator.md@4f8fb05601e2bd84cf2fb05741ff5a868f285c6b")

        assertThat(identifier.path).isEqualTo("Calculator.md")
        assertThat(identifier.revision).isEqualTo("4f8fb05601e2bd84cf2fb05741ff5a868f285c6b")
    }

    @Test
    fun `throws if too many separators`() {
        assertThrows<InvalidDocumentIdentifierException> {
            GitDocumentIdentifier.parse("file@revision@revision")
        }
    }

    @Test
    fun `ignores empty revision`() {
        val identifier = GitDocumentIdentifier.parse("Calculator.md")

        assertThat(identifier.path).isEqualTo("Calculator.md")
        assertThat(identifier.revision).isEqualTo("HEAD")
    }
}
