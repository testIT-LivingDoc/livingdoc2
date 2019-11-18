package org.livingdoc.repositories.file

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.BeforeRow
import org.livingdoc.api.fixtures.decisiontables.Check
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.api.fixtures.decisiontables.Input
import org.livingdoc.repositories.Document

@ExecutableDocument("local://FileRepositoryIntegrationTest.md")
class FileRepositoryIntegrationTest {

    @DecisionTableFixture
    class FileRepositoryDecisionTableFixture {

        private lateinit var cut: FileRepository

        @Input("File-Name")
        private var fileName: String = ""

        @BeforeRow
        fun beforeRow() {
            cut = FileRepository("", FileRepositoryConfig("src/test/resources"))
        }

        @Check("Throws FileNotFoundException")
        fun checkOutput(expectedValue: Boolean) {
            if (expectedValue) {
                assertThrows<FileDocumentNotFoundException> {
                    cut.getDocument(fileName)
                }
                return
            }
            assertThat(cut.getDocument(fileName)).isInstanceOf(Document::class.java)
        }
    }
}
