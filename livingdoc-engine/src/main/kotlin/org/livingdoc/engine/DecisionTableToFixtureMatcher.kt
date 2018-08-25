package org.livingdoc.engine

import org.livingdoc.engine.execution.examples.decisiontables.DecisionTableFixtureModel
import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.decisiontable.Header
import kotlin.reflect.KClass

/**
 * Default matcher to find the right fixture classes for a given list of tables.
 */
class DecisionTableToFixtureMatcher {

    /**
     * Finds the fitting fixture for the given decision tables.
     */
    fun matchTablesToFixtures(tables: List<DecisionTable>, fixtures: List<KClass<*>>): Map<DecisionTable, Class<*>> {
        /*
         * - Start with a list of Tables and a list of Fixtures
         * - Create mapping Model -> Fixture (straightforward)
         * - Create mapping Table -> Model (find table that contains all aliases of the model)
         * - Create mapping Table -> Fixture (via Table -> Model -> Fixture)
         */
        val fixtureModelToFixtureClass = mapModelsToFixtures(fixtures)
        val decisionTableToFixtureModel = mapTablesToModels(fixtureModelToFixtureClass.keys, tables)
        return mapTablesToFixtures(decisionTableToFixtureModel, fixtureModelToFixtureClass)
    }

    private fun mapTablesToFixtures(
            decisionTableToFixtureModel: Map<DecisionTable, DecisionTableFixtureModel?>,
            fixtureModelToFixtureClass: Map<DecisionTableFixtureModel, Class<*>?>
    ): Map<DecisionTable, Class<*>> =
            decisionTableToFixtureModel.keys.map { table ->
                val model: DecisionTableFixtureModel = decisionTableToFixtureModel[table]
                        ?: throw NoDecisionTableModelForDecisionTable(table)
                val fixture: Class<*> = fixtureModelToFixtureClass[model]
                        ?: throw NoFixtureForDecisionTableModelException(model)
                table to fixture
            }.toMap()

    private fun mapModelsToFixtures(fixtureClasses: List<KClass<*>>) =
            fixtureClasses.map { it.java }.associateByTo(mutableMapOf()) { clazz ->
                DecisionTableFixtureModel(clazz)
            }

    private fun mapTablesToModels(tableModels: Iterable<DecisionTableFixtureModel>, tables: Iterable<DecisionTable>) =
            tableModels.associateByTo(mutableMapOf()) { model ->
                val table = tables.firstOrNull { (headers, _) ->
                    headers.map(Header::name).containsAll(model.inputAliases)
                } ?: throw NoDecisionTableForDecisionTableModelException(model)
                table
            }
}

/**
 * This exception is thrown when the [DecisionTableToFixtureMatcher] cannot find a [DecisionTable] that fits
 * to a [DecisionTableFixtureModel] of a Fixture class.
 */
class NoDecisionTableForDecisionTableModelException(model: DecisionTableFixtureModel)
    : java.lang.IllegalArgumentException("Could not find a Decision Table for Fixture Class ${model.fixtureClass.canonicalName}")

/**
 * This exception is thrown when the [DecisionTableToFixtureMatcher] cannot find a [DecisionTableFixtureModel] that fits
 * to a [DecisionTable].
 */
class NoDecisionTableModelForDecisionTable(table: DecisionTable)
    : java.lang.IllegalArgumentException("Could not find a Decision Table Model for Decision Table $table")

/**
 * This exception is thrown when the [DecisionTableToFixtureMatcher] cannot find a Fixture class that fits
 * to a [DecisionTableFixtureModel].
 */
class NoFixtureForDecisionTableModelException(model: DecisionTableFixtureModel)
    : java.lang.IllegalArgumentException("Could not find a Decision Table Model for Decision Table ${model.fixtureClass.canonicalName}")

