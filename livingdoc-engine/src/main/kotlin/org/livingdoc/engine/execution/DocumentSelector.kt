package org.livingdoc.engine.execution

/**
 * Describes an executable document to be found by LivingDoc.
 *
 * @since 2.0
 */
data class DocumentSelector(
        /** @return the unique resource identifier (URI) of the document */
        val uri: String
)
