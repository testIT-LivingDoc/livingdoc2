package org.livingdoc.repositories.format

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.livingdoc.repositories.*
import java.io.InputStream
import java.nio.charset.Charset

class HtmlFormat : DocumentFormat {

    override fun parse(stream: InputStream): HtmlDocument {
        val streamContent = stream.readBytes().toString(Charset.defaultCharset())
        val document = Jsoup.parse(streamContent)
        return HtmlDocument(parseTables(document), emptyList(), document)
    }

    private fun parseTables(document: org.jsoup.nodes.Document): List<DecisionTable> {
        val tableElements = document.getElementsByTag("table")
        return parseTableElements(tableElements)
    }

    private fun parseTableElements(tableElements: Elements): List<DecisionTable> {
        fun tableHasAtLeastTwoRows(table: Element) = table.getElementsByTag("tr").size > 1

        return tableElements
                .filter(::tableHasAtLeastTwoRows)
                .map(::parseTableToDecisionTable)
    }

    private fun parseTableToDecisionTable(table: Element): DecisionTable {
        val tableRows = table.getElementsByTag("tr")
        val headers = extractHeadersFromFirstRow(tableRows)
        val dataRows = parseDataRow(headers, tableRows)
        return DecisionTable(headers, dataRows)
    }

    private fun extractHeadersFromFirstRow(tableRows: Elements): List<String> {
        val firstRowContainingHeaders = tableRows[0]
        val headers = firstRowContainingHeaders.children()
                .filter(::isHeaderOrDataCell)
                .map(Element::text).toList()

        if (headers.size != headers.distinct().size) {
            throw ParseException("Headers must contains only unique values: " + headers)
        }
        return headers
    }

    private fun parseDataRow(headers: List<String>, tableRows: Elements): List<DecisionTableRow> {
        val dataRows = mutableListOf<DecisionTableRow>()
        tableRows.drop(1).forEachIndexed { rowIndex, row ->
            val dataCells = row.children().filter(::isHeaderOrDataCell)

            if (headers.size != dataCells.size) {
                throw ParseException("Header count must match the data cell count in data row ${rowIndex + 1}. Headers: $headers, DataCells: $dataCells")
            }

            val rowData = headers.mapIndexed { headerIndex, headerName ->
                headerName to DecisionTableCell(dataCells[headerIndex].text())
            }.toMap()
            dataRows.add(DecisionTableRow(rowData))
        }
        return dataRows
    }

    private fun isHeaderOrDataCell(it: Element) = it.tagName() == "th" || it.tagName() == "td"
}
