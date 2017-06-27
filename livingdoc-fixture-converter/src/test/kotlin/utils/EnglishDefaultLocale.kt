package utils

import org.junit.jupiter.api.extension.ExtendWith


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(EnglishDefaultLocaleExtension::class)
annotation class EnglishDefaultLocale
