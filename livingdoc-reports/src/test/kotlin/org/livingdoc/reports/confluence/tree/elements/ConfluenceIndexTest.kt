package org.livingdoc.reports.confluence.tree.elements

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult

internal class ConfluenceIndexTest {
    @Test
    fun `render summary for tags`() {
        val documents = listOf(
            DocumentResult.Builder()
                .withDocumentClass(ConfluenceIndexTest::class.java)
                .withStatus(Status.Executed)
                .withTags(listOf("slow", "api"))
                .build(),

            DocumentResult.Builder()
                .withDocumentClass(ConfluenceIndexTest::class.java)
                .withStatus(Status.Failed(mockk(relaxed = true)))
                .withTags(listOf("slow"))
                .build(),

            DocumentResult.Builder()
                .withDocumentClass(ConfluenceIndexTest::class.java)
                .withStatus(Status.Manual)
                .withTags(listOf("performance"))
                .build()
        )

        assertThat(ConfluenceIndex(documents).toString()).isEqualToIgnoringWhitespace(
            """
                <table>
                    <thead>
                        <tr>
                            <th>Tag</th>
                            <th>✅</th>
                            <th>❔</th>
                            <th>❌</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><i>all tags</i></td>
                            <td>1</td>
                            <td>1</td>
                            <td>1</td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(0, 128, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 0, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 102, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                        <tr>
                            <td>slow</td>
                            <td>1</td>
                            <td>0</td>
                            <td>1</td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(0, 128, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 0, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                        <tr>
                            <td>api</td>
                            <td>1</td>
                            <td>0</td>
                            <td>0</td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(0, 128, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                        <tr>
                            <td>performance</td>
                            <td>0</td>
                            <td>1</td>
                            <td>0</td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 102, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                    </tbody>
                </table>
            """
        )
    }
}
