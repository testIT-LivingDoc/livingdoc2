package org.livingdoc.converters;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import kotlin.reflect.KType;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.api.conversion.Context;
import org.livingdoc.api.conversion.ConversionException;
import org.livingdoc.api.conversion.Converter;
import org.livingdoc.api.conversion.TypeConverter;
import org.livingdoc.api.documents.ExecutableDocument;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TypeConvertersTestFixtures {

    public static class AnnotatedMethodParameter {

        public void method(@Converter(CustomBooleanConverter.class) Boolean value) {
        }
    }

    public static class AnnotatedMethod {

        @Converter(CustomBooleanConverter.class)
        public void method(Boolean value) {
        }
    }

    public static class AnnotatedField {

        @Converter(CustomBooleanConverter.class)
        public Boolean field;

    }

    @Converter(CustomBooleanConverter.class)
    public static class AnnotatedClass {

        public Boolean field;

        public void method(Boolean value) {
        }
    }

    public static class NoAnnotations {

        public Boolean field;
        public List<Boolean> listField;
        public Set<Boolean> setField;
        public Map<String, Boolean> mapField;

        public void method(Boolean value) {
        }
    }

    @ExecutableDocument("abc-1")
    public static class DocumentWithoutAnnotation {

    }

    @ExecutableDocument("abc-2")
    @Converter(CustomBooleanConverter.class)
    public static class DocumentWithAnnotation {

    }

    public static class CustomBooleanConverter implements TypeConverter<Boolean> {

        @Override
        public Boolean convert(@NotNull String value, @NotNull KType type, @NotNull Context context) throws ConversionException {
            return "true".equalsIgnoreCase(value);
        }

        @Override
        public boolean canConvertTo(@NotNull KClass<?> targetType) {
            return Boolean.class.isAssignableFrom(JvmClassMappingKt.getJavaClass(targetType));
        }
    }
}
