package org.livingdoc.repositories.model.scenario

import org.livingdoc.repositories.model.Example

data class Scenario(
        val steps: List<Step>
) : Example
