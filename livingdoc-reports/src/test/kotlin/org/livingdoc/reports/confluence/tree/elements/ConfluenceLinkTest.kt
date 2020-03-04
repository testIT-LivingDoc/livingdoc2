package org.livingdoc.reports.confluence.tree.elements

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.livingdoc.results.Status

internal class ConfluenceLinkTest {
    @Test
    fun `check correct format of confluence link`() {
        assertThat(ConfluenceLink("test", Status.Executed).toString()).isEqualToIgnoringWhitespace(
            """
                <ac:link>
                    <ri:page ri:content-title="test"></ri:page>
                    <ac:link-body>
                        <span style="color: rgb(0, 128, 0);">test</span>
                    </ac:link-body>
                </ac:link>
            """
        )
    }
}
