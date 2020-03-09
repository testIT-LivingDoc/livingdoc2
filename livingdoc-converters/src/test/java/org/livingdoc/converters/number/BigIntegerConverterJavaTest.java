package org.livingdoc.converters.number;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import kotlin.jvm.JvmClassMappingKt;
import org.junit.jupiter.api.Test;

import org.livingdoc.converters.TypeConverterExtensionKt;
import utils.EnglishDefaultLocale;


@EnglishDefaultLocale
class BigIntegerConverterJavaTest {

    BigIntegerConverter cut = new BigIntegerConverter();

    @Test
    void converterCanConvertedToJavaBigInteger() {
        assertThat(cut.canConvertTo(JvmClassMappingKt.getKotlinClass(BigInteger.class))).isTrue();
    }

    @Test
    void javaInteroperabilityIsWorking() {
        BigInteger value = TypeConverterExtensionKt.convertValueOnly(cut,"42");
        assertThat(value).isEqualTo(BigInteger.valueOf(42L));
    }

}
