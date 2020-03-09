package org.livingdoc.repositories.git

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.lib.Constants
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("This test requires configuration of a remote git repository")
class GitRepositoryResolverTest {
    private val config = GitRepositoryConfig()

    @Test
    fun `works with existing repository`() {
        val cut = GitRepositoryResolver(config)

        assertThat(cut.resolve()).satisfies { repository ->
            assertThat(repository.directory).satisfies { directory ->
                assertThat(directory.exists()).isTrue()
                assertThat(directory.isDirectory).isTrue()

                assertThat(directory.list()).isNotEmpty()
            }

            assertThat(repository.isBare)

            assertThat(repository.resolve(Constants.HEAD).name)
                .isEqualTo("dce1d5e2b50955adc7cf0ce79cc7dcb789a81c35")
        }
    }

    @Test
    fun `clones non existent repository`() {
        val cut = GitRepositoryResolver(config)

        assertThat(cut.resolve()).satisfies { repository ->
            assertThat(repository.directory).satisfies { directory ->
                assertThat(directory.exists()).isTrue()
                assertThat(directory.isDirectory).isTrue()

                assertThat(directory.list()).isNotEmpty()
            }

            assertThat(repository.isBare)

            assertThat(repository.resolve(Constants.HEAD).name)
                .isEqualTo("dce1d5e2b50955adc7cf0ce79cc7dcb789a81c35")
        }
    }
}
