package org.livingdoc.reports.confluence.attachment

import com.atlassian.confluence.api.model.content.id.ContentId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult

internal class ConfluenceAttachmentReportRendererTest {

    private val doc1Result = DocumentResult.Builder()
        .withDocumentClass(TestDocument1::class.java)
        .withStatus(Status.Executed)
        .withTags(emptyList())
        .build()

    private val doc2Result = DocumentResult.Builder()
        .withDocumentClass(TestDocument2::class.java)
        .withStatus(Status.Executed)
        .withTags(emptyList())
        .build()

    private val doc3Result = DocumentResult.Builder()
        .withDocumentClass(TestDocument3::class.java)
        .withStatus(Status.Executed)
        .withTags(emptyList())
        .build()

    private val reportRenderer =
        ConfluenceAttachmentReportRenderer()

    @Test
    fun `extractContentId$livingdoc_livingdoc_reports_main`() {
        assertThat(reportRenderer.extractContentId(doc1Result)).isEqualTo(ContentId.of(12345))

        assertThrows<IllegalArgumentException> {
            reportRenderer.extractContentId(doc2Result)
        }

        assertThrows<IllegalArgumentException> {
            reportRenderer.extractContentId(doc3Result)
        }
    }

    @Test
    fun `extractRepositoryName$livingdoc_livingdoc_reports_main`() {
        assertThat(reportRenderer.extractRepositoryName(doc1Result)).isEqualTo("confluence-test")

        assertThrows<IllegalArgumentException> {
            reportRenderer.extractRepositoryName(doc2Result)
        }

        assertThrows<IllegalArgumentException> {
            reportRenderer.extractRepositoryName(doc3Result)
        }
    }
}

@ExecutableDocument("confluence-test://12345")
class TestDocument1

@ExecutableDocument("invalid")
class TestDocument2

class TestDocument3
