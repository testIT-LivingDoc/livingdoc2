package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.DecisionTable
import org.livingdoc.repositories.ParseException
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlOrderedListWithNestedOrderedList
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlOrderedListWithNestedUnorderedList

import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlTableWithNonUniqueHeaders
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlTableWithOnlyOneRow
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlTableWithWrongCellCount
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlUnorderedListWithNestedOrderedList
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlWithOrderedList
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlWithOrderedListContainsOnlyOneItem
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlWithUnorderedList
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlWithUnorderedListContainsOnlyOneItem
import org.livingdoc.repositories.format.HtmlFormatTestData.getValidHtml
import org.livingdoc.repositories.format.HtmlFormatTestData.getHtmlUnorderedListWithNestedUnorderedList


class HtmlFormatTest {

    private val cut = HtmlFormat()

    @Test
    fun `tables with one row are ignored`() {
        var result = cut.parse(getHtmlTableWithOnlyOneRow())
        assertThat(result.tables).hasSize(0);
    }

    @Test
    fun `exception if headers are not unique`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlTableWithNonUniqueHeaders())
        }

        assertThat(exception).hasMessageStartingWith("Headers must contains only unique values:")
    }

    @Test
    fun `exception if header count not equal to data cell count`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlTableWithWrongCellCount())
        }

        assertThat(exception).hasMessageStartingWith("Header count must match the data cell count in data row 1. Headers: [Firstname, Lastname, Age], DataCells: [<td>Jill </td>, <td>Thomsen </td>]")
    }

    @Test
    fun `parse Html into DecisionTable`() {
        var htmlDocument = cut.parse(getValidHtml())

        var documentNode = htmlDocument.tables[0]

        assertThat(documentNode).isInstanceOf(DecisionTable::class.java)
        val decisionTable = documentNode as DecisionTable
        assertThat(decisionTable.headers).containsExactly("Firstname", "Lastname", "Age")

        assertThat(decisionTable.rows).hasSize(2);
        assertThat(decisionTable.rows[0].cells).hasSize(3)
        assertThat(decisionTable.rows[1].cells).hasSize(3)

        assertThat(decisionTable.rows[0].cells.map { it.key }).containsExactly("Firstname", "Lastname", "Age")
        assertThat(decisionTable.rows[0].cells.map { it.value.text }).containsExactly("Jill", "Smith", "50")

        assertThat(decisionTable.rows[1].cells.map { it.key }).containsExactly("Firstname", "Lastname", "Age")
        assertThat(decisionTable.rows[1].cells.map { it.value.text }).containsExactly("Eve", "Jackson", "94")
    }

    @Test
    fun `parse unorderedList into Scenario`() {
        var htmlDocument = cut.parse(getHtmlWithUnorderedList())

        var scenario = htmlDocument.lists[0]

        assertThat(scenario.steps).isNotNull
        assertThat(scenario.steps).hasSize(5)
        assertThat(scenario.steps[0]).isEqualTo("First list item")
        assertThat(scenario.steps[1]).isEqualTo("Second list item")
        assertThat(scenario.steps[2]).isEqualTo("Third list item")
        assertThat(scenario.steps[3]).isEqualTo("Fourth list item")
        assertThat(scenario.steps[4]).isEqualTo("Fifth list item")
    }

    @Test
    fun `parse orderedList into Scenario`() {
        var htmlDocument = cut.parse(getHtmlWithOrderedList())
        var scenario = htmlDocument.lists[0]

        assertThat(scenario.steps).isNotNull
        assertThat(scenario.steps).hasSize(5)
        assertThat(scenario.steps[0]).isEqualTo("First list item")
        assertThat(scenario.steps[1]).isEqualTo("Second list item")
        assertThat(scenario.steps[2]).isEqualTo("Third list item")
        assertThat(scenario.steps[3]).isEqualTo("Fourth list item")
        assertThat(scenario.steps[4]).isEqualTo("Fifth list item")
    }

    @Test
    fun `unordered list with only one item is ignored`() {
        var htmlDocument = cut.parse(getHtmlWithUnorderedListContainsOnlyOneItem())

        assertThat(htmlDocument.lists).isNotNull
        assertThat(htmlDocument.lists).isEmpty()
    }

    @Test
    fun `ordered list with only one item is ignored`() {
        var htmlDocument = cut.parse(getHtmlWithOrderedListContainsOnlyOneItem())

        assertThat(htmlDocument.lists).isNotNull
        assertThat(htmlDocument.lists).isEmpty()
    }

    @Test
    fun `exception if unordered list contains nested unordered list`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlUnorderedListWithNestedUnorderedList())
        }

        assertThat(exception).hasMessageStartingWith("Nested lists within unordered or ordered lists are not supported:")
    }

    @Test
    fun `exception if unordered list contains nested ordered list`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlUnorderedListWithNestedOrderedList())
        }

        assertThat(exception).hasMessageStartingWith("Nested lists within unordered or ordered lists are not supported:")
    }

    @Test
    fun `exception if ordered list contains nested unordered list`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlOrderedListWithNestedUnorderedList())
        }

        assertThat(exception).hasMessageStartingWith("Nested lists within unordered or ordered lists are not supported:")
    }

    @Test
    fun `exception if ordered list contains nested ordered list`() {
        var exception = assertThrows(ParseException::class.java) {
            cut.parse(getHtmlOrderedListWithNestedOrderedList())
        }

        assertThat(exception).hasMessageStartingWith("Nested lists within unordered or ordered lists are not supported:")
    }
}
