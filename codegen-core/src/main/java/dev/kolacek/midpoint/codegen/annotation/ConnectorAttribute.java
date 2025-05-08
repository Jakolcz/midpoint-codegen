package dev.kolacek.midpoint.codegen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify details about a connector attribute.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ConnectorAttribute {

    boolean DEFAULT_REQUIRED = false;
    boolean DEFAULT_MULTIVALUED = false;

    /**
     * The name of the attribute, if empty, name of the field is used.
     *
     * @return Name of the attribute.
     */
    String value() default "";

    boolean required() default DEFAULT_REQUIRED;

    boolean multivalued() default DEFAULT_MULTIVALUED;
}
