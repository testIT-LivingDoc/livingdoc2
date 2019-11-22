package org.livingdoc.repositories.format

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.HtmlDocument
import org.livingdoc.repositories.ParseException
import org.livingdoc.repositories.file.ParseContext
import org.livingdoc.repositories.model.TestData
import org.livingdoc.repositories.model.TestDataDescription
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step
import java.io.InputStream
import java.nio.charset.Charset

class HtmlFormat : DocumentFormat {

    private val supportedFileExtensions = setOf("html", "htm")

    override fun canHandle(fileExtension: String): Boolean {
        return supportedFileExtensions.contains(fileExtension.toLowerCase())
    }

    override fun parse(stream: InputStream): HtmlDocument {
        val streamContent = stream.readBytes().toString(Charset.defaultCharset())
        val document = Jsoup.parse(streamContent)
        val elements = parseRecursive(document.body(), ParseContext())
        return HtmlDocument(elements, document)
    }

    private fun parseRecursive(root: Element, rootContext: ParseContext): List<TestData> {
        fun tableHasAtLeastTwoRows(table: Element) = table.getElementsByTag("tr").size > 1
        fun listHasAtLeastTwoItems(htmlList: Element) = htmlList.getElementsByTag("li").size > 1
        var context = rootContext

        return root.children().flatMap {
            when (it.tagName()) {
                "h1", "h2", "h3", "h4", "h5", "h6" -> {
                    context = ParseContext(it.text())
                    emptyList()
                }
                "table" -> {
                    if (tableHasAtLeastTwoRows(it))
                        listOf(parseTableToDecisionTable(it, context))
                    else emptyList()
                }
                "ul", "ol" -> {
                    parseRecursive(it, context) +
                    if (listHasAtLeastTwoItems(it))
                        listOf(parseListIntoScenario(it, context))
                    else emptyList()
                }
                else -> parseRecursive(it, context)
            }
        }
    }

    private fun parseTableToDecisionTable(table: Element, context: ParseContext): DecisionTable {
        val tableRows = table.getElementsByTag("tr")
        val headers = extractHeadersFromFirstRow(tableRows)
        val dataRows = parseDataRow(headers, tableRows)
        return DecisionTable(headers, dataRows, TestDataDescription(context.headline, false))
    }

    private fun extractHeadersFromFirstRow(tableRows: Elements): List<Header> {
        val firstRowContainingHeaders = tableRows[0]
        val headers = firstRowContainingHeaders.children()
            .filter(::isHeaderOrDataCell)
            .map(Element::text)
            .map(::Header).toList()

        if (headers.size != headers.distinct().size) {
            throw ParseException("Headers must contains only unique values: $headers")
        }
        return headers
    }

    private fun parseDataRow(headers: List<Header>, tableRows: Elements): List<Row> {
        val dataRows = mutableListOf<Row>()
        tableRows.drop(1).forEachIndexed { rowIndex, row ->
            val dataCells = row.children().filter(::isHeaderOrDataCell)

            if (headers.size != dataCells.size) {
                throw ParseException(
                    "Header count must match the data cell count in data row ${rowIndex + 1}. " +
                            "Headers: ${headers.map(Header::name)}, DataCells: $dataCells"
                )
            }

            val rowData = headers.mapIndexed { headerIndex, headerName ->
                headerName to Field(dataCells[headerIndex].text())
            }.toMap()
            dataRows.add(Row(rowData))
        }
        return dataRows
    }

    private fun isHeaderOrDataCell(it: Element) = it.tagName() == "th" || it.tagName() == "td"

    private fun parseListIntoScenario(htmlList: Element, context: ParseContext): Scenario {
        verifyZeroNestedLists(htmlList)

        val listItemElements = htmlList.getElementsByTag("li")
        return Scenario(parseListItems(listItemElements), TestDataDescription(context.headline, isManual(context)))
    }

    private fun parseListItems(listItemElements: Elements): List<Step> {
        return listItemElements.map { Step(it.text()) }.toList()
    }

    private fun verifyZeroNestedLists(htmlList: Element) {
        val innerHtml = htmlList.html()
        if (innerHtml.contains("<ul") || innerHtml.contains("<ol")) {
            throw ParseException("Nested lists within unordered or ordered lists are not supported: ${htmlList.html()}")
        }
    }

    private fun isManual(context: ParseContext): Boolean {
        // == true needed for null check
        return context.headline?.contains("MANUAL") == true
    }
}
