package fixtures;

import java.lang.reflect.AnnotatedElement;

import org.livingdoc.fixture.api.binding.ExecutableDocument;
import org.livingdoc.fixture.api.converter.ConversionException;
import org.livingdoc.fixture.api.converter.Converter;
import org.livingdoc.fixture.api.converter.TypeConverter;


public class TypeConvertersTestFixtures {

    public static class AnnotatedMethodParameter {

        public void method(@Converter(CustomBooleanConverter.class) Boolean value) {
            System.out.println(value);
        }

    }

    public static class AnnotatedMethod {

        @Converter(CustomBooleanConverter.class)
        public void method(Boolean value) {
            System.out.println(value);
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
            System.out.println(value);
        }

    }

    public static class NoAnnotations {

        public Boolean field;

        public void method(Boolean value) {
            System.out.println(value);
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
        public Boolean convert(String value, AnnotatedElement element) throws ConversionException {
            return "true".equalsIgnoreCase(value);
        }

        @Override
        public boolean canConvertTo(Class<?> targetType) {
            return Boolean.class.isAssignableFrom(targetType);
        }

    }

}
