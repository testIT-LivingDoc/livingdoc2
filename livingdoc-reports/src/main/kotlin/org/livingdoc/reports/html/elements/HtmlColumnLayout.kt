package org.livingdoc.reports.html.elements

import org.livingdoc.results.documents.DocumentResult
import java.nio.file.Path

class HtmlColumnLayout(columns: HtmlColumnLayout.() -> Unit) : HtmlElement("div") {

    init {
        cssClass("flex")
        child {
            HtmlElement(
                "script",
                """function collapse (indicator, row) {
                        var indicatorElem = document.getElementById(indicator);
                        var rowElem = document.getElementById(row);
                        if (rowElem.classList.contains("hidden")) {
                            indicatorElem.innerHTML = "⏷";
                        } else {
                            indicatorElem.innerHTML = "⏵";
                        }
                        rowElem.classList.toggle("hidden");
                    }"""
            )
        }
        columns()
    }
}

/**
 * This creates the left column for the index/summary page with a title and a list of all documents
 *
 * @param reports a list of all reports that were generated in this test run
 */
fun HtmlColumnLayout.indexList(reports: List<Pair<DocumentResult, Path>>) {
    child {
        HtmlElement("div") {
            cssClass("flex-50")
            child { HtmlTitle("Index") }

            child {
                HtmlList {
                    linkList(reports)
                }
            }
        }
    }
}

/**
 *  This creates the right column with the tag table
 *
 * @param reports a list of all reports that were generated in this test run
 */
fun HtmlColumnLayout.tagList(reports: List<Pair<DocumentResult, Path>>) {
    child {
        HtmlElement("div") {
            cssClass("flex-50")
            child { HtmlTitle("Tag summary") }

            val reportsByTag = reports.flatMap { report ->
                listOf(
                    listOf("all" to report),
                    report.first.tags.map { tag ->
                        tag to report
                    }
                ).flatten()
            }.groupBy({ it.first }, { it.second })

            child {
                HtmlTable {
                    attr("id", "summary-table")
                    summaryTableHeader()

                    reportsByTag.map { (tag, documentResults) ->
                        tagRow(tag, documentResults)
                        collapseRow(tag, documentResults)
                    }
                }
            }
        }
    }
}
