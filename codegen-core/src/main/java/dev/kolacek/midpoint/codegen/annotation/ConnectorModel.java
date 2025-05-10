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
     *
     * @return The name of the object class.
     * @see org.identityconnectors.framework.common.objects.ObjectClass
     * @see org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder#setType(String)
     */
    String objectClassType() default "__ACCOUNT__";
}
