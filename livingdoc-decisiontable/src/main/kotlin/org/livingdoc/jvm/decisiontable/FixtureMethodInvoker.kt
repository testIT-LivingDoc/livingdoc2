package org.livingdoc.jvm.decisiontable

import kotlin.reflect.KCallable

object FixtureMethodInvoker {

    fun invoke(method: KCallable<*>, vararg parameters: String) {
        TODO("Convert kparameter with typeconverter to correct type")
        val call = method.call(parameters)
    }
}
