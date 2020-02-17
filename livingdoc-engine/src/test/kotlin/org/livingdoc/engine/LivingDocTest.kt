package org.livingdoc.engine

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.livingdoc.config.ConfigProvider
import org.livingdoc.engine.execution.MalformedFixtureException
import org.livingdoc.engine.resources.DeclaringGroup.AmbiguousGroupExecutableDocument
import org.livingdoc.engine.resources.DisabledExecutableDocument
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.results.Status

internal class LivingDocTest {

    @Test
    fun disabledExecutableDocumentExecute() {
        val repoManagerMock = mockkJClass(RepositoryManager::class.java)
        val configProviderMock = ConfigProvider(emptyMap())
        val cut = LivingDoc(configProviderMock, repoManagerMock)
        val documentClass = DisabledExecutableDocument::class.java

        val results = cut.execute(listOf(documentClass))

        assertThat(results).hasSize(1)

        val result = results[0]
        assertThat(result.documentStatus).isInstanceOf(Status.Disabled::class.java)
        assertThat((result.documentStatus as Status.Disabled).reason).isEqualTo("Skip this test document")
    }

    @Test
    fun `ambiguous group is detected`() {
        val repoManagerMock = mockkJClass(RepositoryManager::class.java)
        val configProviderMock = ConfigProvider(emptyMap())
        val cut = LivingDoc(configProviderMock, repoManagerMock)
        val documentClass = AmbiguousGroupExecutableDocument::class.java

        val exception = assertThrows<MalformedFixtureException> {
            cut.execute(listOf(documentClass))
        }

        assertThat(exception.message).contains("AmbiguousGroupExecutableDocument", "DeclaringGroup", "AnnotationGroup")
    }
}
