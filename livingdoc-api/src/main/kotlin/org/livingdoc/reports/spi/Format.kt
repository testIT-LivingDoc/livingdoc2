package org.livingdoc.reports.spi

/**
 * Specifies the format of a [ReportRenderer], by the unique identifier [value].
 * This annotation can only be used on [ReportRenderer].
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class Format(val value: String)
