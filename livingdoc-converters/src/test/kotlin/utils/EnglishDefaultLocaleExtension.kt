package utils

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.*
import java.util.Locale.Category.DISPLAY
import java.util.Locale.Category.FORMAT
import java.util.Locale.ENGLISH
import java.util.Locale.setDefault

class EnglishDefaultLocaleExtension : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        with(context.globalStore) {
            put("defaultLocale", Locale.getDefault())
            put("defaultFormatLocale", Locale.getDefault(FORMAT))
            put("defaultDisplayLocale", Locale.getDefault(DISPLAY))
        }
        setDefault(ENGLISH)
        setDefault(FORMAT, ENGLISH)
        setDefault(DISPLAY, ENGLISH)
    }

    override fun afterEach(context: ExtensionContext) {
        with(context.globalStore) {
            Locale.setDefault(get("defaultLocale") as Locale?)
            Locale.setDefault(FORMAT, get("defaultFormatLocale") as Locale?)
            Locale.setDefault(DISPLAY, get("defaultDisplayLocale") as Locale?)
        }
    }

    private val ExtensionContext.globalStore: ExtensionContext.Store
        get() = this.getStore(ExtensionContext.Namespace.GLOBAL)
}
