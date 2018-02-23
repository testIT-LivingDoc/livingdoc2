package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.DocumentNode
import org.livingdoc.repositories.ParseException


class HtmlFormatTest {

    private val cut = HtmlFormat()

    @Test fun `tables with one row are ignored`() {
        var result = cut.parse(getHtmlTableWithOnlyOneRow())
        assertThat(result.content).hasSize(0);
    }

    @Test fun `exception if headers are not unique`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlTableWithNonUniqueHeaders())
        }

        assertThat(exception).hasMessageStartingWith("Headers must contains only unique values:")
    }

    @Test fun `exception if header count not equal to data cell count`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlTableWithWrongCellCount())
        }

        assertThat(exception).hasMessageStartingWith("Header count must match the data cell count in data row 1. Headers: [Firstname, Lastname, Age], DataCells: [<td>Jill </td>, <td>Thomsen </td>]")
    }

    @Test fun `parse Html into DecisionTable`() {
        var htmlDocument = cut.parse(getValidHtml())

        var documentNode = htmlDocument.content[0]

        assertThat(documentNode).isInstanceOf(DocumentNode.DecisionTable::class.java)
        val decisionTable = documentNode as DocumentNode.DecisionTable
        assertThat(decisionTable.headers).containsExactly("Firstname", "Lastname", "Age")

        assertThat(decisionTable.rows).hasSize(2);
        assertThat(decisionTable.rows[0].cells).hasSize(3)
        assertThat(decisionTable.rows[1].cells).hasSize(3)

        assertThat(decisionTable.rows[0].cells.map { it.key }).containsExactly("Firstname", "Lastname", "Age")
        assertThat(decisionTable.rows[0].cells.map { it.value.text }).containsExactly("Jill", "Smith", "50")

        assertThat(decisionTable.rows[1].cells.map { it.key }).containsExactly("Firstname", "Lastname", "Age")
        assertThat(decisionTable.rows[1].cells.map { it.value.text }).containsExactly("Eve", "Jackson", "94")
    }

    private fun getHtmlTableWithOnlyOneRow() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <table style="width:100%">
            <tr>
                <th>Firstname</th>
                <th>Lastname</th>
                <th>Age</th>
            </tr>
        </table>
    </body>
    </html>
            """.byteInputStream()

    private fun getHtmlTableWithNonUniqueHeaders() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <table>
            <tr>
                <th>Firstname</th>
                <th>Lastname</th>
                <th>Lastname</th>
            </tr>
            <tr>
                <td>Jill</th>
                <td>Thomsen</th>
                <td>35</th>
            </tr>
        </table>
    </body>
    </html>
            """.byteInputStream()

    private fun getHtmlTableWithWrongCellCount() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <body>
        <table>
            <tr>
                <th>Firstname</th>
                <th>Lastname</th>
                <th>Age</th>
            </tr>
            <tr>
                <td>Jill</th>
                <td>Thomsen</th>
            </tr>
        </table>
    </body>
    </html>
            """.byteInputStream()

    private fun getValidHtml() =
            """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>LivingDoc-HTML Parser</title>
    </head>
    <body>
    <h1>Headline First Order</h1>

    SIMPLE TEXT

    <p>
        PARAGRAPH CONTENT
    </p>
    <div>CONTENT OF THE DIV</div>

    <ul>
        <li>
            text1
            <p>
                List Paragraph
            </p>
            <table style="width:100%">
                <tr>
                    <th>Firstname</th>
                    <th>Lastname</th>
                    <th>Age</th>
                </tr>
                <tr>
                    <td>Jill</td>
                    <td>Smith</td>
                    <td>50</td>
                </tr>
                <tr>
                    <td>Eve</td>
                    <td>Jackson</td>
                    <td>94</td>
                </tr>
            </table>
            text111
        </li>
        <li>text2</li>
        <li>text3</li>
    </ul>

    <ol>
        <li>text4</li>
        <li>text5</li>
        <li>text6</li>
    </ol>

    <ol type = "">
        <li>text7</li>
        <li>text8</li>
        <li>text9</li>
    </ol>

    <ol type = "A">
        <li>textX</li>
        <li>textY</li>
        <li>textZ</li>
    </ol>

    </body>
    </html>
    """.byteInputStream()

}

