package org.livingdoc.repositories.format

/**
 * A context class used during parsing of documents
 * It helps mapping a headline to all following test cases
 */
data class ParseContext(
    val headline: String? = null,
    val descriptiveText: String = ""
) {
    /**
     * Checks whether the current [ParseContext] indicates a manual test
     */
    fun isManual(): Boolean {
        return headline?.contains("MANUAL") ?: false
    }
}
