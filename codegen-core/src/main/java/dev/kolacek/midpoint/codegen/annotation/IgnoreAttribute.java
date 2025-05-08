package dev.kolacek.midpoint.codegen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be ignored during code generation.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface IgnoreAttribute {
}
