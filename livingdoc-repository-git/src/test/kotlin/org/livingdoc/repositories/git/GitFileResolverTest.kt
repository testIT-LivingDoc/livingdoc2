package org.livingdoc.repositories.git

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.DocumentNotFoundException
import java.io.File

@Disabled("This test requires configuration of a local git repository")
internal class GitFileResolverTest {
    private val cut = GitFileResolver(
        FileRepositoryBuilder()
            .setBare()
            .setGitDir(
                File(
                    GitFileResolverTest::class.java.classLoader.getResource("livingdoc-specifications.git").toURI()
                )
            )
            .build()
    )

    @Test
    fun `finds existing file`() {
        val file = cut.resolve(GitDocumentIdentifier("TestTexts.md"))

        assertThat(file).hasContent(
            """
                # Scenarios

                # Test A

                - concatenate {a} and {b} will result in {a}{b}
                - concatenate ddd and dd will result in ddddd
                - concatenate ddd and cc will result in dddcc

                # Test B

                - concatenate () and () will result in ()()

                # Test C

                this test will also run with strange characters

                - concatenate bla and 〈〉 will result in bla〈〉

                # Test D

                - nullifying cdf and rising will give us 0.0F as output
            """
        )
    }

    @Test
    fun `finds previous version of file`() {
        val file = cut.resolve(GitDocumentIdentifier("TestTexts.md", "4f8fb05601e2bd84cf2fb05741ff5a868f285c6b"))

        assertThat(file).hasContent(
            """
                # Scenarios

                # Test A

                - concatenate {a} and {b} will result in {a}{b}
                - concatenate ddd and dd will result in ddddd

                # Test B

                - concatenate () and () will result in ()()

                # Test C

                this test will also run with strange characters

                - concatenate bla and 〈〉 will result in bla〈〉

                # Test D

                - nullifying cdf and rising will give us 0.0F as output
            """
        )
    }

    @Test
    fun `throws exception when file cannot be found`() {
        val path = "Calculator.md"

        val exception = assertThrows<DocumentNotFoundException> {
            cut.resolve(GitDocumentIdentifier(path))
        }

        assertThat(exception)
            .hasMessageContaining(path)
            .hasMessageContaining("Could not find")
    }

    @Test
    fun `throws exception when path is directory`() {
        val path = "Calculator"

        val exception = assertThrows<DocumentNotFoundException> {
            cut.resolve(GitDocumentIdentifier(path))
        }

        assertThat(exception)
            .hasMessageContaining("is a directory")
            .hasMessageContaining(path)
    }
}
