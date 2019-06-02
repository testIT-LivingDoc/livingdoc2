package org.livingdoc.repositories.format

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.scenario.Scenario

internal class MarkdownFormatTest {

    @Test
    fun `Table is read successfully`() {
        val mdFormat = MarkdownFormat()
        val document = mdFormat.parse(
                """
            # Irrelevant Headline

            ```
            Irrelevant codeblock
            ```

            ## Irrelevant Headline

            Irrelevant Text

            | Column1 | Column2 | Column3 |
            |---------|---------|---------|
            | Cell11  | Cell12  | Cell13  |
            | Cell21  | Cell22  | Cell23  |
        """.trimIndent().byteInputStream()
        )

        val elements = document.elements

        assertThat(elements).hasSize(1)

        val firstElement = elements[0] as DecisionTable
        assertThat(firstElement.headers.map(Header::name)).containsExactly("Column1", "Column2", "Column3")
        firstElement.rows.also { rows ->
            assertThat(rows[0].headerToField.map { it.key.name }).containsExactly("Column1", "Column2", "Column3")
            assertThat(rows[0].headerToField.map { it.value.value }).containsExactly("Cell11", "Cell12", "Cell13")

            assertThat(rows[1].headerToField.map { it.key.name }).containsExactly("Column1", "Column2", "Column3")
            assertThat(rows[1].headerToField.map { it.value.value }).containsExactly("Cell21", "Cell22", "Cell23")
        }
    }

    @Test
    fun `Bullet list is read successfully`() {
        val mdFormat = MarkdownFormat()
        val document = mdFormat.parse(
                """
            # Irrelevant Headline

            ```
            Irrelevant codeblock
            ```

            ## Irrelevant Headline

            Irrelevant Text

             * Listitem1
               Sentence in first list item.
             * Listitem2
               Sentence in second list item.
             * Listitem3
               Sentence in third list item.

               Another sentence in third list item.


               This sentence ends with a period.
               And continues in the next line.



               A final sentence in the third item, separated by many newlines.
             * Listitem4
                 Sentence in fourth list item.
        """.trimIndent().byteInputStream()
        )

        val elements = document.elements

        assertThat(elements).hasSize(1)

        val thirdSentence = """
            Listitem3
            Sentence in third list item.
            Another sentence in third list item.

            This sentence ends with a period.
               And continues in the next line.

            A final sentence in the third item, separated by many newlines.

            """.trimIndent()

        val scenario = elements[0] as Scenario
        scenario.steps.also {
            assertThat(it).hasSize(4)
            assertThat(it[0].value).isEqualTo("Listitem1\nSentence in first list item.")
            assertThat(it[1].value).isEqualTo("Listitem2\nSentence in second list item.")
            assertThat(it[2].value).isEqualTo(thirdSentence)
            assertThat(it[3].value).isEqualTo("Listitem4\nSentence in fourth list item.")
        }
    }

    @Test
    fun `Numbered list is read successfully`() {
        val mdFormat = MarkdownFormat()
        val document = mdFormat.parse(
                """
            # Irrelevant Headline

            ```
            Irrelevant codeblock
            ```

            ## Irrelevant Headline

            Irrelevant Text

             1. Listitem1
                Sentence in first list item.
             2. Listitem2
                Sentence in second list item.
             3. Listitem3
                Sentence in third list item.

                Another sentence in third list item.


                This sentence ends with a period.
                And continues in the next line.



                A final sentence in the third item, separated by many newlines.
             4. Listitem4
                Sentence in fourth list item.
        """.trimIndent().byteInputStream()
        )

        val elements = document.elements

        assertThat(elements).hasSize(1)

        val thirdSentence = """
            Listitem3
            Sentence in third list item.
            Another sentence in third list item.

            This sentence ends with a period.
                And continues in the next line.

            A final sentence in the third item, separated by many newlines.

            """.trimIndent()

        val scenario = elements[0] as Scenario
        assertThat(scenario.steps).hasSize(4)
        assertThat(scenario.steps[0].value).isEqualTo("Listitem1\nSentence in first list item.")
        assertThat(scenario.steps[1].value).isEqualTo("Listitem2\nSentence in second list item.")
        assertThat(scenario.steps[2].value).isEqualTo(thirdSentence)
        assertThat(scenario.steps[3].value).isEqualTo("Listitem4\nSentence in fourth list item.")
    }

    @Test
    fun `Column table is read as scenario`() {
        val mdFormat = MarkdownFormat()
        val document = mdFormat.parse(
                """
            # Irrelevant Headline

            ```
            Irrelevant codeblock
            ```

            ## Irrelevant Headline

            Irrelevant Text

            | Column1 |
            |---------|
            | Cell11  |
            | Cell21  |
        """.trimIndent().byteInputStream()
        )

        val elements = document.elements

        assertThat(elements).hasSize(1)

        val scenario = elements[0] as Scenario
        scenario.steps.also {
            assertThat(it).hasSize(3)
            assertThat(it[0].value).isEqualTo("Column1")
            assertThat(it[1].value).isEqualTo("Cell11")
            assertThat(it[2].value).isEqualTo("Cell21")
        }
    }

    @Test
    fun `Tables and Scenarios are read in the order they appear in`() {
        val mdFormat = MarkdownFormat()
        val document = mdFormat.parse(
                """
            # Irrelevant Headline

            Irrelevant Text

            | Column1 |
            |---------|
            | Cell11  |
            | Cell21  |

            1. Listitem1
               Sentence in first list item.
            2. Listitem2
               Sentence in second list item.

            | ColumnA | ColumnB |
            |---------|---------|
            | CellA1  | CellB1  |
            | CellA2  | CellB2  |
        """.trimIndent().byteInputStream()
        )

        val elements = document.elements

        assertThat(elements).hasSize(3)

        val scenario1 = elements[0] as Scenario
        scenario1.steps.also {
            assertThat(it).hasSize(3)
            assertThat(it[0].value).isEqualTo("Column1")
            assertThat(it[1].value).isEqualTo("Cell11")
            assertThat(it[2].value).isEqualTo("Cell21")
        }

        val scenario2 = elements[1] as Scenario
        scenario2.steps.also {
            assertThat(it).hasSize(2)
            assertThat(it[0].value).isEqualTo("Listitem1\nSentence in first list item.")
            assertThat(it[1].value).isEqualTo("Listitem2\nSentence in second list item.")
        }

        val table = elements[2] as DecisionTable
        assertThat(table.headers.map(Header::name)).containsExactly("ColumnA", "ColumnB")
        table.rows.also { rows ->
            assertThat(rows[0].headerToField.map { it.key.name }).containsExactly("ColumnA", "ColumnB")
            assertThat(rows[0].headerToField.map { it.value.value }).containsExactly("CellA1", "CellB1")

            assertThat(rows[1].headerToField.map { it.key.name }).containsExactly("ColumnA", "ColumnB")
            assertThat(rows[1].headerToField.map { it.value.value }).containsExactly("CellA2", "CellB2")
        }
    }
}
