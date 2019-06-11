package org.livingdoc.engine

import io.mockk.clearMocks
import io.mockk.mockkClass

fun clearJMockk(mock: Any) = clearMocks(mock, mock)

fun <T: Any> mockkJClass(type: Class<T>): T {
    return mockkClass(type.kotlin, relaxed = true)
}
