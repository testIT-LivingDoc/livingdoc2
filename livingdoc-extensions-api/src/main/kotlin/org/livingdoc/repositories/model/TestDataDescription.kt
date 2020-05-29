package org.livingdoc.repositories.model

/**
 * Stores additional data for each TestData instance
 */
data class TestDataDescription(
    val name: String?,
    val isManual: Boolean,
    val descriptiveText: String
)
