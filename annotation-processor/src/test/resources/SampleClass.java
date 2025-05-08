package dev.kolacek.generator.test;

import dev.kolacek.midpoint.codegen.annotation.ConnectorAttribute;
import dev.kolacek.midpoint.codegen.annotation.ConnectorObject;

@ConnectorObject
public class SampleClass {

    @ConnectorAttribute(value = "annotationName", required = true)
    private String name;
    private String description;
    private int primitiveInt;
    private Integer integerValue;
    private SampleEnum enumValue;

    private enum SampleEnum {
        VALUE1,
        VALUE2
    }
}