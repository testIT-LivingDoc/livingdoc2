package org.livingdoc.repositories.git

import org.junit.jupiter.api.Test
import org.livingdoc.repositories.DocumentRepositoryFactory

class GitRepositoryFactoryTest {
    private val cut: DocumentRepositoryFactory<GitRepository> = GitRepositoryFactory()

    @Test
    fun `can create git repository`() {
        cut.createRepository("git", emptyMap())
    }
}
