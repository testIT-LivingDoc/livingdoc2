package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element
import org.livingdoc.results.documents.DocumentResult
import java.nio.file.Path

class HtmlColumnLayout(columns: HtmlColumnLayout.() -> Unit) : HtmlElement("div") {

    init {
        addClass { "flex" }
        appendChild {
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
    appendChild {
        HtmlElement("div") {
            addClass { "flex-50" }
            appendChild { HtmlTitle("Index") }
            appendChild {
                HtmlList {
                    linkList(reports)
                }
            }
        }
    }
}

/**
 *  This returns the right column with the tag table
 *
 * @param reports a list of all reports that were generated in this test run
 * @return a String containing HTML code of the right column (table with sublist per tag)
 */
fun HtmlColumnLayout.tagList(reports: List<Pair<DocumentResult, Path>>) {
    val tagListDiv = Element("div").addClass("flex-50")

    tagListDiv.appendChild(Element("h2").html("Tag Summary"))

    val reportsByTag = reports.flatMap { report ->
        listOf(
            listOf("all" to report),
            report.first.tags.map { tag ->
                tag to report
            }
        ).flatten()
    }.groupBy({ it.first }, { it.second })

    val tagTable = Element("table").attr("id", "summary-table")
    tagTable.appendChild(summaryTableHeader())

    reportsByTag.map { (tag, documentResults) ->
        tagTable.appendChild(tagRow(tag, documentResults))
        tagTable.appendChild(collapseRow(tag, documentResults))
    }

    tagListDiv.appendChild(tagTable)

    // TODO rework
    appendHtml {
        tagListDiv.toString()
    }
}
