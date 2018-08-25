package org.livingdoc.engine

import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableExecutor
import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureModel
import org.livingdoc.repositories.RepositoryManager
import org.livingdoc.repositories.config.Configuration
import org.livingdoc.repositories.model.decisiontable.DecisionTable

/**
 * Executes the given document class and returns the [DocumentResult]. The document's class must be annotated
 * with [ExecutableDocument].
 *
 * @return the [DocumentResult] of the execution
 * @throws ExecutionException in case the execution failed in a way that did not produce a viable result
 * @since 2.0
 */
class LivingDoc(val tableMatcher: DecisionTableToFixtureMatcher = DecisionTableToFixtureMatcher()) {

    private fun validateInputAndMatchFixtures(document: Class<*>): Map<DecisionTable, Class<*>> {
        if (!document.isAnnotationPresent(ExecutableDocument::class.java)) {
            throw IllegalArgumentException("ExecutableDocument annotation is not present on class ${document.canonicalName}.")
        }

        val annotation = document.getAnnotation(ExecutableDocument::class.java)
        val values = annotation.value.split("://")
        if (values.size != 2) throw IllegalArgumentException("Illegal annotation value '${annotation.value}'.")

        val (name, id) = values
        val repository = RepositoryManager.from(Configuration.load()).getRepository(name)
        val (tables, _) = repository.getDocument(id)

        val fixtureClasses = annotation.fixtureClasses
                .filter { it.java.isAnnotationPresent(DecisionTableFixture::class.java) }

        return tableMatcher.matchTablesToFixtures(tables, fixtureClasses)

    }

    @Throws(ExecutionException::class)
    fun execute(document: Class<*>): DocumentResult {
        val tableToFixture = validateInputAndMatchFixtures(document)

        val executor = DecisionTableExecutor()
        val decisionTableResults = tableToFixture.map { (table, fixture) ->
            executor.execute(table, fixture)
        }

        return DocumentResult(decisionTableResults = decisionTableResults)
    }

}
