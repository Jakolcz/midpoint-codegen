package dev.kolacek.midpoint.codegen.util;

import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;

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
