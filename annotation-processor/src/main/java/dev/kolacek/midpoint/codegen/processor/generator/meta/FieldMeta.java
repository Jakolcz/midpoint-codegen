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

package dev.kolacek.midpoint.codegen.processor.generator.meta;

import com.palantir.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;
import java.util.Objects;
import java.util.Optional;

public class FieldMeta {

    private String name;
    private String getterName;
    private TypeName fieldType;
    private boolean required;
    private boolean multivalued;
    private ExecutableElement getter;
    private EnumMeta enumMeta;

    public FieldMeta() {
    }

    public FieldMeta(String name, String getterName, TypeName fieldType, boolean required, boolean multivalued, ExecutableElement getter, EnumMeta enumMeta) {
        this.name = name;
        this.getterName = getterName;
        this.fieldType = fieldType;
        this.required = required;
        this.multivalued = multivalued;
        this.getter = getter;
        this.enumMeta = enumMeta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGetterName() {
        return getterName;
    }

    public void setGetterName(String getterName) {
        this.getterName = getterName;
    }

    public TypeName getFieldType() {
        return fieldType;
    }

    public void setFieldType(TypeName fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isMultivalued() {
        return multivalued;
    }

    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }

    public Optional<ExecutableElement> getGetter() {
        return Optional.ofNullable(getter);
    }

    public void setGetter(ExecutableElement getter) {
        this.getter = getter;
    }

    public Optional<EnumMeta> getEnumMeta() {
        return Optional.ofNullable(enumMeta);
    }

    public void setEnumMeta(EnumMeta enumMeta) {
        this.enumMeta = enumMeta;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FieldMeta fieldMeta)) return false;
        return required == fieldMeta.required && multivalued == fieldMeta.multivalued && Objects.equals(name, fieldMeta.name) && Objects.equals(getterName, fieldMeta.getterName) && Objects.equals(fieldType, fieldMeta.fieldType) && Objects.equals(getter, fieldMeta.getter) && Objects.equals(enumMeta, fieldMeta.enumMeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, getterName, fieldType, required, multivalued, getter, enumMeta);
    }

    @Override
    public String toString() {
        return "FieldMeta{" +
                "name='" + name + '\'' +
                ", getterName='" + getterName + '\'' +
                ", fieldType=" + fieldType +
                ", required=" + required +
                ", multivalued=" + multivalued +
                ", getter=" + getter +
                ", enumMeta=" + enumMeta +
                '}';
    }
}
