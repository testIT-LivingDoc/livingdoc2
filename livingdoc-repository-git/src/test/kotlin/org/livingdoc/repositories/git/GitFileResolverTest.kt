package org.livingdoc.repositories.git

import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.repositories.DocumentNotFoundException
import java.io.File

class GitFileResolverTest {
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
        val content = cut.resolve("Calculator/Calculator.feature")

        assertThat(String(content.readAllBytes())).isEqualToIgnoringWhitespace(
            """
                Feature: Calculator
                  Scenario: The calculator can add
                    Given a calculator
                    When I add 2 and 3
                    Then I get 5
                    But result is less than 10
                    And result is greater than 0
            """
        )
    }

    @Test
    fun `throws exception when file cannot be found`() {
        val path = "Calculator.md"

        val exception = assertThrows<DocumentNotFoundException> {
            cut.resolve(path)
        }

        assertThat(exception)
            .hasMessageContaining(path)
            .hasMessageContaining("Could not find")
    }

    @Test
    fun `throws exception when path is directory`() {
        val path = "Calculator"

        val exception = assertThrows<DocumentNotFoundException> {
            cut.resolve(path)
        }

        assertThat(exception)
            .hasMessageContaining("is a directory")
            .hasMessageContaining(path)
    }
}
