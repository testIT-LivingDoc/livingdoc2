package utils

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.*
import java.util.Locale.*
import java.util.Locale.Category.DISPLAY
import java.util.Locale.Category.FORMAT

class EnglishDefaultLocaleExtension : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        with(context.store) {
            put("defaultLocale", getDefault())
            put("defaultFormatLocale", getDefault(FORMAT))
            put("defaultDisplayLocale", getDefault(DISPLAY))
        }
        setDefault(ENGLISH)
        setDefault(FORMAT, ENGLISH)
        setDefault(DISPLAY, ENGLISH)
    }

    override fun afterEach(context: ExtensionContext) {
        val store = context.store
        setDefault(store.get("defaultLocale") as Locale?)
        setDefault(FORMAT, store.get("defaultFormatLocale") as Locale?)
        setDefault(DISPLAY, store.get("defaultDisplayLocale") as Locale?)
    }

}
