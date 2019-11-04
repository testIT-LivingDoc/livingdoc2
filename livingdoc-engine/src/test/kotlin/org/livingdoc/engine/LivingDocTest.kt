package org.livingdoc.engine

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.livingdoc.engine.execution.Result
import org.livingdoc.engine.resources.DisabledExecutableDocument
import org.livingdoc.repositories.RepositoryManager

internal class LivingDocTest {

    @Test
    fun disabledExecutableDocumentExecute() {
        val repoManagerMock = mockkJClass(RepositoryManager::class.java)
        val cut = LivingDoc(repoManagerMock)
        val documentClass = DisabledExecutableDocument::class.java

        val result = cut.execute(documentClass)

        assertThat(result.documentResult).isInstanceOf(Result.Disabled::class.java)
        assertThat((result.documentResult as Result.Disabled).reason).isEqualTo("Skip this test document")
    }
}
