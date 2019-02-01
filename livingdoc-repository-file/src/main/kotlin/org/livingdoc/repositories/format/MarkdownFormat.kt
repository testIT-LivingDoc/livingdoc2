package org.livingdoc.repositories.format

import com.vladsch.flexmark.ast.ListBlock
import com.vladsch.flexmark.ast.Node
import com.vladsch.flexmark.ast.Paragraph
import com.vladsch.flexmark.ast.SoftLineBreak
import com.vladsch.flexmark.ast.Text
import com.vladsch.flexmark.ext.tables.TableBlock
import com.vladsch.flexmark.ext.tables.TableCell
import com.vladsch.flexmark.ext.tables.TableRow
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import org.livingdoc.repositories.Document
import org.livingdoc.repositories.DocumentFormat
import org.livingdoc.repositories.ParseException
import org.livingdoc.repositories.model.Example
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.repositories.model.decisiontable.Row
import org.livingdoc.repositories.model.scenario.Scenario
import org.livingdoc.repositories.model.scenario.Step
import java.io.InputStream
import java.util.Arrays.asList

/**
 * [DocumentFormat] implementation specializing in markdown.
 * Based on the flexmark parser: [https://github.com/vsch/flexmark-java].
 */
class MarkdownFormat : DocumentFormat {

    override fun canHandle(fileExtension: String): Boolean {
        return fileExtension.toLowerCase() == "md"
    }

    override fun parse(stream: InputStream): Document {
        val options = MutableDataSet()
        options.set(Parser.EXTENSIONS, asList(TablesExtension.create()))
        val parser = Parser.builder(options).build()
        val text = stream.reader().use { it.readText() }
        val examples = parser.parse(text).mapToNodes()
        return Document(examples)
    }

    private fun com.vladsch.flexmark.ast.Node.mapToNodes(): List<Example> {
        val examples = mutableListOf<Example>()

        this.children.toList().forEach { node ->
            when (node) {
                is ListBlock -> examples.add(node.toScenario())
                is TableBlock -> {
                    val firstRow = node.firstChild.children.toList()[0]
                    val numberOfColumns = firstRow.children.toList().size
                    when (numberOfColumns > 1) {
                        true -> examples.add(node.toDecisionTable())
                        else -> examples.add(node.toScenario())
                    }
                }
            }
        }

        return examples
    }

    private fun Node.getAllChildrenOfListItem(): List<Node> {
        val children = mutableListOf<Node>()
        val first = this.children.first()
        when (first) {
            is Paragraph -> children.addAll(first.children.toList())
            else -> throw ParseException("First item of List Node ${first.chars} is not a Paragraph")
        }
        children.addAll(this.children.drop(1))
        return children
    }

    private fun ListBlock.toScenario(): Scenario {
        val textItems = this.children.toList().map { child ->
            val listItemChildren = child.getAllChildrenOfListItem()
            val text = StringBuilder((listItemChildren.first() as Text).chars.toString())
            with(text) {
                listItemChildren.drop(1).map { item ->
                    when (item) {
                        is Paragraph -> append("\n" + item.chars.toString())
                        is Text -> append("\n" + item.chars.toString())
                        is SoftLineBreak -> {
                        }
                        else -> throw ParseException("List Item '${item.chars}' is not a plain text.")
                    }
                }
            }
            Step(text.toString())
        }
        return Scenario(textItems)
    }

    private fun TableBlock.toScenario(): Scenario {
        val tableChildren = this.children.toList()
        val textItems = mutableListOf<Step>()

        val tableHeadRow = tableChildren[0].children.toList()[0]
        tableHeadRow.children.verifyElementType<TableCell> { throw ParseException("Element $it is not a TableCell.") }
        textItems.add(Step((tableHeadRow.children.first() as TableCell).text.toString()))

        tableChildren[2].children.toList().forEach { row ->
            row.children.verifyElementType<TableCell> { throw ParseException("Element $it is not a TableCell.") }
            textItems.add(Step((row.children.first() as TableCell).text.toString()))
        }

        return Scenario(textItems)
    }

    private fun TableBlock.toDecisionTable(): DecisionTable {
        val children = this.children.toList()
        // the second item is the separator
        val tableHeadChildren = children[0].children.toList()
        val bodyChildren = children[2].children.toList()

        tableHeadChildren.verifyElementType<TableRow> { throw ParseException("Element $it is not a TableRow.") }
        bodyChildren.verifyElementType<TableRow> { throw ParseException("Element $it is not a TableRow.") }

        val headers = tableHeadChildren[0].let { node ->
            node.children.verifyElementType<TableCell> { throw ParseException("Element $it is not a TableCell.") }
            node.children.map { cell -> Header((cell as TableCell).text.toString()) }
        }

        val rows = bodyChildren.map { node ->
            val row = node as TableRow
            row.children.verifyElementType<TableCell> { throw ParseException("Element $it is not a TableCell.") }
            val map = row.children.mapIndexed { index, childNode ->
                val cell = childNode as TableCell
                headers[index] to Field(cell.text.toString())
            }.toMap()
            Row(map)
        }

        return DecisionTable(headers, rows)
    }

    private inline fun <reified U> Iterable<*>.verifyElementType(errorCallBack: (Any) -> Unit) {
        this.forEach {
            if (it !is U) errorCallBack(it!!)
        }
    }
}
