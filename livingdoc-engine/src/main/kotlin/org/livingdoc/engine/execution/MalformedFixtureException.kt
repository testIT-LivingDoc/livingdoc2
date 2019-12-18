package org.livingdoc.engine.execution

import java.lang.RuntimeException

/**
 * MalformedFixtureException is thrown when a fixture does not parse validation
 */
internal class MalformedFixtureException(fixtureClass: Class<*>, errors: List<String>) :
    RuntimeException(
        "The fixture class <$fixtureClass> is malformed: \n${errors.joinToString(
            separator = "\n",
            prefix = "  - "
        )}")
