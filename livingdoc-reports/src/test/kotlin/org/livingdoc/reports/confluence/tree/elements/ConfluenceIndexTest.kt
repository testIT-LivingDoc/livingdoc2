package org.livingdoc.reports.confluence.tree.elements

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import java.time.Duration

internal class ConfluenceIndexTest {
    @Test
    fun `render summary for tags`() {
        val documents = listOf(
            DocumentResult.Builder()
                .withDocumentClass(ConfluenceIndexTest::class.java)
                .withStatus(Status.Executed)
                .withTags(listOf("slow", "api"))
                .withTime(Duration.ofMillis(332))
                .build(),

            DocumentResult.Builder()
                .withDocumentClass(ConfluenceIndexTest::class.java)
                .withStatus(Status.Failed(mockk(relaxed = true)))
                .withTags(listOf("slow"))
                .withTime(Duration.ofMillis(217))
                .build(),

            DocumentResult.Builder()
                .withDocumentClass(ConfluenceIndexTest::class.java)
                .withStatus(Status.Manual)
                .withTags(listOf("performance"))
                .withTime(Duration.ofMillis(1095))
                .build()
        )

        assertThat(ConfluenceIndex(documents).toString()).isEqualToIgnoringWhitespace(
            """
                <table>
                    <thead>
                        <tr>
                            <th>Tag</th>
                            <th>Time</th>
                            <th>✔</th>
                            <th>✖</th>
                            <th>···</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                <i>all tags</i>
                            </td>
                            <td>${"%.3fs".format(1.644)}</td>
                            <td>1</td>
                            <td class="highlight-red">1</td>
                            <td class="highlight-yellow">1</td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(0, 128, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest (${"%.3fs".format(0.332)})</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 0, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest (${"%.3fs".format(0.217)})</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 102, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest (${"%.3fs".format(1.095)})</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                        <tr>
                            <td>slow</td>
                            <td>${"%.3fs".format(0.549)}</td>
                            <td>1</td>
                            <td class="highlight-red">1</td>
                            <td>0</td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(0, 128, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest (${"%.3fs".format(0.332)})</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 0, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest (${"%.3fs".format(0.217)})</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                        <tr>
                            <td>api</td>
                            <td>${"%.3fs".format(0.332)}</td>
                            <td class="highlight-green">1</td>
                            <td class="highlight-green">0</td>
                            <td class="highlight-green">0</td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(0, 128, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest (${"%.3fs".format(0.332)})</span>
                                            </ac:link-body>
                                        </ac:link>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                        <tr>
                            <td>performance</td>
                            <td>${"%.3fs".format(1.095)}</td>
                            <td>0</td>
                            <td>0</td>
                            <td class="highlight-yellow">1</td>
                        </tr>
                        <tr>
                            <td colspan="5">
                                <ul>
                                    <li>
                                        <ac:link>
                                            <ri:page ri:content-title="org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest"></ri:page>
                                            <ac:link-body>
                                                <span style="color: rgb(255, 102, 0);">org.livingdoc.reports.confluence.tree.elements.ConfluenceIndexTest (${"%.3fs".format(1.095)})</span>
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
