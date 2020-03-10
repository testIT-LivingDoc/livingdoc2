package org.livingdoc.repositories.git

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.DocumentNotFoundException
import org.livingdoc.repositories.DocumentRepository
import org.livingdoc.repositories.cache.CacheConfiguration

@Disabled("This test requires configuration of a remote git repository")
internal class GitRepositoryTest {
    private val cut: DocumentRepository = GitRepository(
        "git", GitRepositoryConfig(
            cache = CacheConfiguration(
                path = createTempDir().absolutePath
            )
        )
    )

    @Test
    fun `can load document file`() {
        assertThat(cut.getDocument("TestTexts.md")).satisfies { document ->
            assertThat(document.elements).hasSize(4)
        }
    }

    @Test
    fun `throws if document cannot be found`() {
        assertThrows<DocumentNotFoundException> {
            cut.getDocument("NonExistentDocument.md")
        }
    }
}
