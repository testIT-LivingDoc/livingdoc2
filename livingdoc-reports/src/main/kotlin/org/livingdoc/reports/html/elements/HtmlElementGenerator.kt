package org.livingdoc.reports.html.elements

import org.jsoup.nodes.Element
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import java.nio.file.Path

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

    calculateSummaryNumbers(documentResults)
        .forEachIndexed { index, number ->
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
        append(HtmlList{linkList(documentResults)}.toString())
    })

    return collapseRow
}
