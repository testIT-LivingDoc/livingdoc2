package org.livingdoc.engine.fixtures

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.livingdoc.engine.fixtures.FixtureMethodInvoker.*
import java.lang.reflect.Method
import kotlin.reflect.KClass


internal class FixtureMethodInvokerTest {

    val cut = FixtureMethodInvoker(null)

    val fixture = TestFixture()

    @Nested inner class `all kinds of methods can be executed` {

        @Test fun `method without parameters`() {
            val method = getMethod("withoutParameters")
            val result = cut.invoke(method, fixture)
            assertThat(result).isEqualTo("no params")
        }

        @Test fun `method with one parameter`() {
            val method = getMethod("oneParameter", Boolean::class)
            val result = cut.invoke(method, fixture, arrayOf("true"))
            assertThat(result).isEqualTo("param: true")
        }

        @Test fun `method with two parameters`() {
            val method = getMethod("twoParameters", Boolean::class, Boolean::class)
            val result = cut.invoke(method, fixture, arrayOf("true", "false"))
            assertThat(result).isEqualTo("firstParam: true, secondParam: false")
        }

        @Test fun `method with no return value`() {
            val method = getMethod("noReturnValue")
            val result = cut.invoke(method, fixture)
            assertThat(result).isNull()
        }

    }

    @Nested inner class `exception handling` {

        @Test fun `custom exception in case type converter could not be found`() {
            val method = getMethod("typeConverterNotFound", CustomType::class)
            val exception = assertThrows(FixtureMethodInvocationException::class.java, {
                cut.invoke(method, fixture, arrayOf("foo"))
            })
            assertThat(exception).hasCauseExactlyInstanceOf(NoTypeConverterFoundException::class.java)
        }

        @Nested inner class `custom exception in case argument number mismatch` {

            @Test fun `to few arguments`() {
                val method = getMethod("oneParameter", Boolean::class)
                val exception = assertThrows(FixtureMethodInvocationException::class.java, {
                    cut.invoke(method, fixture, emptyArray())
                })
                assertThat(exception).hasCauseExactlyInstanceOf(MismatchedNumberOfArgumentsException::class.java)
            }

            @Test fun `to many arguments`() {
                val method = getMethod("oneParameter", Boolean::class)
                val exception = assertThrows(FixtureMethodInvocationException::class.java, {
                    cut.invoke(method, fixture, arrayOf("true", "true"))
                })
                assertThat(exception).hasCauseExactlyInstanceOf(MismatchedNumberOfArgumentsException::class.java)
            }

        }

    }

    @ValueSource(strings = ["privateMethod", "protectedMethod", "publicMethod"])
    @ParameterizedTest fun `method visibility is ignored`(methodName: String) {
        assertThat(cut.invoke(getMethod(methodName), fixture)).isEqualTo("worked")
    }

    fun getMethod(name: String, vararg parameterTypes: KClass<*>): Method {
        val javaParameterTypes = parameterTypes.map { it.java }.toTypedArray()
        return TestFixture::class.java.getDeclaredMethod(name, *javaParameterTypes)
    }

    open class TestFixture {

        fun withoutParameters(): String {
            return "no params"
        }

        fun oneParameter(param: Boolean): String {
            return "param: $param"
        }

        fun twoParameters(firstParam: Boolean, secondParam: Boolean): String {
            return "firstParam: $firstParam, secondParam: $secondParam"
        }

        fun noReturnValue() {
        }

        private fun privateMethod() = "worked"
        protected fun protectedMethod() = "worked"
        fun publicMethod() = "worked"

        fun typeConverterNotFound(param: CustomType) = "$param"

    }

    class CustomType

}
