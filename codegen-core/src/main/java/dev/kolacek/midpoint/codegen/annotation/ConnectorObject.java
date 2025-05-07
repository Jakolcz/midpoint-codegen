package dev.kolacek.midpoint.codegen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface ConnectorObject {


    /**
     * The ObjectClass type of the connector object, default is {@code __ACCOUNT__}, which is {@link org.identityconnectors.framework.common.objects.ObjectClass#ACCOUNT_NAME}.
     *
     * @return The name of the object class.
     * @see org.identityconnectors.framework.common.objects.ObjectClass
     * @see org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder#setType(String)
     */
    String objectClassType() default "__ACCOUNT__";
}
