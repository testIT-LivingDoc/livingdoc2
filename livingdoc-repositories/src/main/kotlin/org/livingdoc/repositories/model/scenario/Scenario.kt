package org.livingdoc.repositories.model.scenario

import org.livingdoc.repositories.model.TestData

data class Scenario(
    val steps: List<Step>
) : TestData
