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

package dev.kolacek.midpoint.codegen.util;

import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;

/**
 * Utility class for creating {@link AttributeInfo} objects.
 */
public final class ObjectClassInfoBuilderUtil {

    /**
     * Creates AttributeInfo object with given name, marked as required, with type String, single valued.
     *
     * @param name The name of the attribute.
     * @return The AttributeInfo
     * @see #createAttributeInfo(String, boolean, Class, boolean)
     */
    public static AttributeInfo createAttributeInfo(String name) {
        return createAttributeInfo(name, true);
    }

    /**
     * Creates AttributeInfo object with given name and required flag, with type String, single valued.
     *
     * @param name     The name of the attribute.
     * @param required If the field is required or not.
     * @return The AttributeInfo
     * @see #createAttributeInfo(String, boolean, Class, boolean)
     */
    public static AttributeInfo createAttributeInfo(String name, boolean required) {
        return createAttributeInfo(name, required, String.class);
    }

    /**
     * Creates AttributeInfo object with given name. required flag and type, single valued.
     *
     * @param name     The name of the attribute.
     * @param required If the field is required or not.
     * @param clazz    The class of the Attribute
     * @return The AttributeInfo
     * @see #createAttributeInfo(String, boolean, Class, boolean)
     */
    public static AttributeInfo createAttributeInfo(String name, boolean required, Class<?> clazz) {
        return createAttributeInfo(name, required, clazz, false);
    }

    /**
     * Creates AttributeInfo object with given params.
     *
     * @param name        The name of the attribute.
     * @param required    If the field is required or not.
     * @param clazz       The class of the Attribute
     * @param multiValued If the Attribute is multivalued or not.
     * @return The AttributeInfo
     */
    public static AttributeInfo createAttributeInfo(String name, boolean required, Class<?> clazz, boolean multiValued) {
        var builder = new AttributeInfoBuilder(name, clazz);
        builder.setRequired(required);
        builder.setMultiValued(multiValued);

        return builder.build();
    }
}
