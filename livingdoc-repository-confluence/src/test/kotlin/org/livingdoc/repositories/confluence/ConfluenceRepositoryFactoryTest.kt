package org.livingdoc.repositories.confluence

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ConfluenceRepositoryFactoryTest {

    private val sut: ConfluenceRepositoryFactory = ConfluenceRepositoryFactory()

    @Test
    fun testCreateRepositoryEmptyConfig() {
        assertDoesNotThrow { sut.createRepository("someName", emptyMap()) }
    }

    @Test
    fun testCreateRepositoryWithConfig() {
        val config = mapOf(
            "baseURL" to "http://confluence.example.com",
            "path" to "/",
            "username" to "livingdoc",
            "password" to "very good password"
        )
        assertDoesNotThrow { sut.createRepository("someName", config) }
    }
}
