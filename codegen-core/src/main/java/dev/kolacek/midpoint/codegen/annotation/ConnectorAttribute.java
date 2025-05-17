/*
 * Copyright 2025 Jakub Koláček
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.kolacek.midpoint.codegen.annotation;

import dev.kolacek.midpoint.codegen.config.AnnotationDefaults;

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

    /**
     * The name of the attribute, if empty, name of the field is used.
     *
     * @return Name of the attribute.
     */
    String value() default "";

    boolean required() default AnnotationDefaults.ConnectorAttribute.DEFAULT_REQUIRED;

    /**
     * The name of the getter method for this attribute. If not specified, the default getter name will be used, following the JavaBean naming conventions.
     * <p>
     * For example, if the field is named {@code myField}, the default getter name will be {@code getMyField}.
     * Primitive {@code boolean} fields will use {@code isMyField} as the getter name, boxed {@code Boolean} fields will use {@code getMyField}.
     * </p>
     *
     * @return Name of the getter method.
     */
    String getterName() default "";
}
