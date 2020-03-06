package org.livingdoc.reports.html.elements

import org.livingdoc.reports.html.MILLISECONDS_DIVIDER
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.examples.decisiontables.DecisionTableResult
import org.livingdoc.results.examples.scenarios.ScenarioResult
import java.nio.file.Path

class HtmlColumnLayout(columns: HtmlColumnLayout.() -> Unit) : HtmlElement("div") {

    init {
        cssClass("flex flex-row")
        columns()
    }
}

/**
 * This creates the left column for the index/summary page with a title and a list of all documents
 *
 * @param reports a list of all reports that were generated in this test run
 */
fun HtmlColumnLayout.indexList(reports: List<Pair<DocumentResult, Path>>) {
    child {
        HtmlElement("div") {
            cssClass("flex-50 column")
            child { HtmlTitle("Index") }
            child {
                HtmlDescription {
                    attr("class", "indexTableDescription")
                    paragraphs(
                        listOf(
                            "This list gives an overview over all tests in the scope of Living Doc. " +
                                    "Click on a test to get a more detailed report."
                        )
                    )
                }
            }

            child {
                HtmlList {
                    linkList(reports)
                }
            }
        }
    }
}

/**
 *  This creates the right column with the tag table
 *
 * @param reports a list of all reports that were generated in this test run
 */
fun HtmlColumnLayout.tagList(reports: List<Pair<DocumentResult, Path>>) {
    child {
        HtmlElement("div") {
            cssClass("flex-50 column")
            child { HtmlTitle("Tag summary") }
            child {
                HtmlDescription {
                    attr("class", "tagTableDescription")
                    paragraphs(
                        listOf(
                            "This list gives an overview over the tests grouped by tag. " +
                                    "Unfold a row to see a list of tests with the selected tag."
                        )
                    )
                }
            }

            val reportsByTag = reports.flatMap { report ->
                listOf(
                    listOf("all" to report),
                    report.first.tags.map { tag ->
                        tag to report
                    }
                ).flatten()
            }.groupBy({ it.first }, { it.second })

            child {
                HtmlTable {
                    attr("id", "summary-table")
                    summaryTableHeader()

                    reportsByTag.map { (tag, documentResults) ->
                        tagRow(tag, documentResults)
                        collapseRow(tag, documentResults)
                    }
                }
            }
        }
    }
}

fun HtmlColumnLayout.report(documentResult: DocumentResult, context: HtmlErrorContext) {
    child {
        HtmlElement("div") {
            cssClass("column")
            val exampleResult = documentResult.results

            val htmlResults = exampleResult.flatMap { result ->
                when (result) {
                    is DecisionTableResult -> handleDecisionTableResult(result, context)
                    is ScenarioResult -> handleScenarioResult(result)
                    else -> throw IllegalArgumentException("Unknown Result type.")
                }
            }.filterNotNull()

            val timestring = " (" + "%.3f".format(documentResult.time.toMillis() / MILLISECONDS_DIVIDER) + "s)"

            child {
                HtmlTitle(
                    documentResult.documentClass.simpleName + timestring
                )
            }
            child {
                HtmlDescription {
                    content(listOf("tags: ").plus(documentResult.tags.map { tag ->
                        "<span class=\"tag\">$tag</span>"
                    }))
                }
            }

            htmlResults.forEach { htmlResult ->
                child { htmlResult }
            }
        }
    }
}

private fun handleDecisionTableResult(
    decisionTableResult: DecisionTableResult,
    renderContext: HtmlErrorContext
): List<HtmlElement?> {
    val (headers, rows, tableResult) = decisionTableResult
    val name = decisionTableResult.decisionTable.description.name
    val desc = decisionTableResult.decisionTable.description.descriptiveText

    return listOf(
        HtmlTitle(name),
        HtmlDescription {
            paragraphs(desc.split("\n"))
        },
        HtmlTable {
            headers(headers)
            rows(renderContext, headers, rows)
            rowIfTableFailed(renderContext, tableResult, headers.size)
        }
    )
}

private fun handleScenarioResult(scenarioResult: ScenarioResult): List<HtmlElement?> {
    val name = scenarioResult.scenario.description.name
    val desc = scenarioResult.scenario.description.descriptiveText

    return listOf(
        HtmlTitle(name),
        HtmlDescription {
            paragraphs(desc.split("\n"))
        },
        HtmlList {
            steps(scenarioResult.steps)
        }
    )
}
