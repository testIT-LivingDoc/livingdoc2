package org.livingdoc.reports.confluence.tree.elements

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import java.time.Duration

internal class ConfluenceLinkTest {
    @Test
    fun `check correct format of confluence link`() {
        val link = ConfluenceLink(
            DocumentResult.Builder()
                .withDocumentClass(ConfluenceLinkTest::class.java)
                .withStatus(Status.Executed)
                .withTime(Duration.ofMillis(176))
                .withTags(emptyList())
                .build()
        ).toString()

        assertThat(link).isEqualToIgnoringWhitespace(
            """
                <ac:link>
                    <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceLinkTest"></ri:page>
                    <ac:link-body>
                        <span style="color: rgb(0, 128, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceLinkTest (${"%.3fs".format(0.176)})</span>
                    </ac:link-body>
                </ac:link>
            """
        )
    }
}
