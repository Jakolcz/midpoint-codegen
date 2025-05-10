package dev.kolacek.generator.test;

import dev.kolacek.midpoint.codegen.annotation.ConnectorAttribute;
import dev.kolacek.midpoint.codegen.annotation.ConnectorModel;

import java.util.List;

@ConnectorModel
public class SampleClass {

    @ConnectorAttribute(value = "annotationName", required = true)
    private String name;
    private String description;
    private int primitiveInt;
    private Integer integerValue;
    private SampleEnum enumValue;
//    private Integer[] intArrayValue;
//    private List<String> listValue;

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

//    public Integer[] getIntArrayValue() {
//        return intArrayValue;
//    }
//
//    public void setIntArrayValue(Integer[] intArrayValue) {
//        this.intArrayValue = intArrayValue;
//    }

//    public List<String> getListValue() {
//        return listValue;
//    }

//    public void setListValue(List<String> listValue) {
//        this.listValue = listValue;
//    }

    public enum SampleEnum {
        VALUE1,
        VALUE2
    }
}