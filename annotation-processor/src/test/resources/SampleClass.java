package dev.kolacek.generator.test;

import dev.kolacek.midpoint.codegen.annotation.ConnectorAttribute;
import dev.kolacek.midpoint.codegen.annotation.ConnectorModel;

@ConnectorModel
public class SampleClass {

    @ConnectorAttribute(value = "annotationName", required = true)
    private String name;
    private String description;
    private int primitiveInt;
    private Integer integerValue;
    private SampleEnum enumValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrimitiveInt() {
        return primitiveInt;
    }

    public void setPrimitiveInt(int primitiveInt) {
        this.primitiveInt = primitiveInt;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public SampleEnum getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(SampleEnum enumValue) {
        this.enumValue = enumValue;
    }

    private enum SampleEnum {
        VALUE1,
        VALUE2
    }
}