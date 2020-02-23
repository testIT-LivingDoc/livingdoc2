package org.livingdoc.jvm.engine.extension

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.livingdoc.jvm.engine.extension.context.StoreImpl

internal class StoreImplTest {

    @Test
    fun getOrComputeIfAbsent() {
        val store = StoreImpl { null }

        val value = store.getOrComputeIfAbsent("someKey") { "value" }
        assertThat(value).isEqualTo("value")
    }

    @Test
    fun getOrComputeIfAbsentWithAncestor() {
        val store1 = StoreImpl { null }
        val store2 = StoreImpl { store1 }
        store1.put("someKey", "value1")
        val value = store2.getOrComputeIfAbsent("someKey") { fail("This should not be executed") }
        assertThat(value).isEqualTo("value1")
    }

    @Test
    fun getListCombineAncestors() {
        val store = StoreImpl { null }
        store.put("keyToList", listOf(1, 2, 3))
        val list = store.getListCombineAncestors("keyToList")
        assertThat(list).isEqualTo(listOf(1, 2, 3))
    }

    @Test
    fun getListCombineAncestorsWithAncestor() {
        val store1 = StoreImpl { null }
        val store2 = StoreImpl { store1 }
        val store3 = StoreImpl { store2 }

        store1.put("keyToList", listOf(1, 2, 3))
        store3.put("keyToList", listOf(4, 5))

        val list2 = store2.getListCombineAncestors("keyToList")
        assertThat(list2).isEqualTo(listOf(1, 2, 3))

        val list3 = store3.getListCombineAncestors("keyToList")
        assertThat(list3).isEqualTo(listOf(1, 2, 3, 4, 5))
    }
}
