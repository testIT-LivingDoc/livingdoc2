package org.livingdoc.example

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.tagging.Tag

/**
 * The manual test feature also works with Markdown documents
 */
@Tag("markdown")
@ExecutableDocument("local://CalculatorManual.md")
class CalculatorDocumentMdManual {
    // The test will fail since there are no matching fixtures, unless the MANUAL tests are correctly skipped.
}
