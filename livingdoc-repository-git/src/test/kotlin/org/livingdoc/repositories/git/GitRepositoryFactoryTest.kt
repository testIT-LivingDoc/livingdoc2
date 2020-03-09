package org.livingdoc.repositories.git

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.DocumentRepositoryFactory

@Disabled("This test requires configuration of a remote git repository")
class GitRepositoryFactoryTest {
    private val cut: DocumentRepositoryFactory<GitRepository> = GitRepositoryFactory()

    @Test
    fun `can create git repository`() {
        assertThat(cut.createRepository("git", emptyMap())).isInstanceOf(GitRepository::class.java)
    }
}
