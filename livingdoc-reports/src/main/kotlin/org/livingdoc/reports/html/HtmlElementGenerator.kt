package org.livingdoc.reports.html

import org.jsoup.nodes.Element
import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import java.nio.file.Path

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
                html("<span class=\"indicator\" id=\"indicator_$tag\" onClick=\"collapse('indicator_$tag'," +
                        "'ID_$tag')\">⏵</span> <i>all tags</i>")
            else
                html("<span class=\"indicator\" id=\"indicator_$tag\" onClick=\"collapse('indicator_$tag'," +
                        "'ID_$tag')\">⏵</span> $tag")
        })

        var numberSuccessful = 0
        var numberFailed = 0
        var numberOther = 0

        documentResults.forEach { (document, _) ->
            when (document.documentStatus) {
                is Status.Executed
                    -> numberSuccessful++
                is Status.Failed
                    -> numberFailed++
                else
                    -> numberOther++
            }
        }

        val summaryNumbers = listOf<Int>(numberSuccessful, numberOther, numberFailed)

        summaryNumbers.forEachIndexed { index, number ->
            tagRow.appendChild(Element("td").apply {
                html(number.toString())
            })
        }

        return tagRow
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
