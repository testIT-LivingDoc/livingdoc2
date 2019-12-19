package org.livingdoc.engine.execution.groups

import org.livingdoc.engine.execution.ScopedFixtureModel

/**
 * A GroupFixtureModel is a representation of the glue code necessary to execute a [GroupFixture]
 *
 * @see GroupFixture
 * @see GroupExecution
 */
internal class GroupFixtureModel(groupClass: Class<*>) : ScopedFixtureModel(groupClass)
