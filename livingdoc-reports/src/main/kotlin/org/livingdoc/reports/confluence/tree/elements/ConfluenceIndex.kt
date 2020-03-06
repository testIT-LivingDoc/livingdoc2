package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.summaryTableHeader
import org.livingdoc.results.documents.DocumentResult

/**
 * ConfluenceIndex is a [ConfluencePage] containing a summary about a test run.
 */
class ConfluenceIndex(reports: List<DocumentResult>) : ConfluencePage() {
    init {
        val reportsByTag = reports.flatMap { report ->
            listOf(
                listOf("all" to report),
                report.tags.map { tag ->
                    tag to report
                }
            ).flatten()
        }.groupBy({ it.first }, { it.second })

        child {
            HtmlTable {
                summaryTableHeader()

                reportsByTag.map { (tag, documentResults) ->
                    cfTagRow(tag, documentResults)
                    cfReportRow(documentResults)
                }
            }
        }
    }
}
