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
import dev.kolacek.midpoint.codegen.config.ReportingPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates that this class should be processed as a connector object and have ObjectClassInfoBuilder and ConnectorObjectInfoBuilder generated.
 * <p>
 * All fields of the class will be processed as connector attributes, unless they are annotated with {@link IgnoreAttribute} annotation.
 * </p>
 *
 * @see ConnectorAttribute
 * @see IgnoreAttribute
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ConnectorModel {


    /**
     * The ObjectClass type of the connector object, default is {@code __ACCOUNT__}, which is {@link org.identityconnectors.framework.common.objects.ObjectClass#ACCOUNT_NAME}.
     * <p>
     * If you want to use one of the default ObjectClass types (ACCOUNT, GROUP, ALL), the value must follow the format used in
     * {@link org.identityconnectors.framework.common.objects.ObjectClassUtil#createSpecialName(String)}, meaning that it must be in uppercase and
     * must start and end with {@code __}.
     * </p>
     *
     * @return The name of the object class.
     * @see org.identityconnectors.framework.common.objects.ObjectClass
     * @see org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder#setType(String)
     */
    String objectClassType() default AnnotationDefaults.ConnectorModel.OBJECT_CLASS_TYPE;

    /**
     * The suffix that will be used for the generated class name, default is {@code Builders}.
     * <p>
     * If the class is named {@code MyClass}, the generated class will be named {@code MyClassBuilders}.
     * </p>
     *
     * @return The suffix that will be used for the generated class name.
     */
    String suffix() default AnnotationDefaults.ConnectorModel.SUFFIX;

    /**
     * The package name that will be used for the generated class, default is the package name of the class.
     *
     * @return The package name that will be used for the generated class.
     */
    String packageName() default "";

    /**
     * The policy that will be used for missing getters, default is {@link ReportingPolicy#WARNING}.
     * <p>
     * If the policy is set to {@link ReportingPolicy#ERROR}, the code generation will fail if a getter is missing.
     * </p>
     *
     * @return The policy that will be used for missing getters.
     */
    ReportingPolicy missingGetterPolicy() default ReportingPolicy.WARNING;

    /**
     * The policy that will be used for unsupported types, default is {@link ReportingPolicy#WARNING}.
     *
     * @return The policy that will be used for unsupported types.
     */
    ReportingPolicy unsupportedTypePolicy() default ReportingPolicy.WARNING;
}
