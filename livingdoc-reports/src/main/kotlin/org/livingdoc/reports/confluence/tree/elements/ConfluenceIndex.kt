package org.livingdoc.reports.confluence.tree.elements

import org.livingdoc.reports.html.elements.HtmlElement
import org.livingdoc.reports.html.elements.HtmlTable
import org.livingdoc.reports.html.elements.summaryTableHeader
import org.livingdoc.results.documents.DocumentResult

class ConfluenceIndex(reports: List<DocumentResult>) : HtmlElement("div") {
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
                    cfReportRow(tag, documentResults)
                }
            }
        }
    }

    override fun toString(): String {
        return element.html()
    }
}
