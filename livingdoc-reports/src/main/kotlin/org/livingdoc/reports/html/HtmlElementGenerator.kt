package org.livingdoc.reports.html

import org.jsoup.nodes.Element
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import java.nio.file.Path

/**
 * This returns the left column for the index/summary page with a title and a list of all documents
 *
 * @param reports a list of all reports that were generated in this test run
 * @return a String containing HTML code of the left column (index list with a list item üer result)
 */
fun renderIndexList(reports: List<Pair<DocumentResult, Path>>): Element {
    val indexListDiv = Element("div").addClass("flex-50")

    indexListDiv.appendChild(Element("h2").html("Index"))

    indexListDiv.appendChild(renderLinkList(reports))
    return indexListDiv
}

/**
 *  This returns the right column with the tag table
 *
 * @param reports a list of all reports that were generated in this test run
 * @return a String containing HTML code of the right column (table with sublist per tag)
 */
fun renderTagList(reports: List<Pair<DocumentResult, Path>>): Element {
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

    return tagListDiv
}

/**
 * This returns an unordered HTML list of Links to DocumentResults
 */
fun renderLinkList(reports: List<Pair<DocumentResult, Path>>): Element {
    val indexList = Element("ul")

    reports.map {
        val listElement = listElement(
            it.first.documentClass.name,
            it.second.fileName.toString(),
            it.first.documentStatus
        )
        indexList.append(listElement.toString())
    }
    return indexList
}

private fun listElement(
    value: String,
    linkAddress: String,
    status: Status
): HtmlListElement {
    return HtmlListElement(
        HtmlLink(
            value, linkAddress, status
        ).toString()
    )
}

/**
 * This returns a HTML node div with a script block for collapsing java script function
 *
 * @return The HTML node containing the script child node
 */
fun generateTwoColumnLayoutWithScript(): Element {
    return Element("div")
        .addClass("flex")
        .appendChild(
            Element("script").html(
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
        )
}

fun summaryTableHeader(): Element {
    val headerRow = Element("tr")
    headerRow.appendChild(Element("th").apply {
        html("Tag")
        // Styling here
    })
    headerRow.appendChild(Element("th").apply {
        html("✅")
        // Styling here
    })
    headerRow.appendChild(Element("th").apply {
        html("❔")
        // Styling here
    })
    headerRow.appendChild(Element("th").apply {
        html("❌")
        // Styling here
    })

    return headerRow
}

fun tagRow(tag: String, documentResults: List<Pair<DocumentResult, Path>>): Element {

    val tagRow = Element("tr")

    tagRow.appendChild(Element("td").apply {
        addClass("tag-cell")
        if (tag == "all")
            html(
                "<span class=\"indicator\" id=\"indicator_$tag\" onClick=\"collapse('indicator_$tag'," +
                        "'ID_$tag')\">⏵</span> <i>all tags</i>"
            )
        else
            html(
                "<span class=\"indicator\" id=\"indicator_$tag\" onClick=\"collapse('indicator_$tag'," +
                        "'ID_$tag')\">⏵</span> $tag"
            )
    })

    calculateSummaryNumbers(documentResults).forEachIndexed { index, number ->
        tagRow.appendChild(Element("td").apply {
            html(number.toString())
        })
    }

    return tagRow
}

/**
 * Calculates the summary numbers for the tag table
 *
 * @param documentResults a list of results to be examined
 * @return a list with three numbers (success, other, failed)
 */
private fun calculateSummaryNumbers(documentResults: List<Pair<DocumentResult, Path>>): List<Int> {
    var numberSuccessful = 0
    var numberFailed = 0
    var numberOther = 0

    documentResults.forEach { (document, _) ->
        when (document.documentStatus) {
            is Status.Executed
            -> numberSuccessful++
            is Status.Failed
            -> numberFailed++
            is Status.Exception
            -> numberFailed++
            else
            -> numberOther++
        }
    }

    return listOf(numberSuccessful, numberOther, numberFailed)
}

/**
 * Renders a collapsible row with a list of document result links
 */
fun collapseRow(tag: String, documentResults: List<Pair<DocumentResult, Path>>): Element {
    val collapseRow = Element("tr")

    collapseRow.attr("id", "ID_$tag")
    collapseRow.addClass("hidden")

    collapseRow.appendChild(Element("td").attr("colspan", "4").apply {
        appendChild(renderLinkList(documentResults))
    })

    return collapseRow
}
