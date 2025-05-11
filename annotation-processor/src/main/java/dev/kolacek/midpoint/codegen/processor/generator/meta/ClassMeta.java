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

import org.identityconnectors.framework.common.objects.ObjectClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ClassMeta {

    private String className;
    private String packageName;
    private String generatedClassName;
    private String generatedPackageName;
    private ObjectClass objectClass;
    private List<FieldMeta> fields;

    public ClassMeta() {
    }

    public ClassMeta(String className, String packageName, String generatedClassName, String generatedPackageName, ObjectClass objectClass, List<FieldMeta> fields) {
        this.className = className;
        this.packageName = packageName;
        this.generatedClassName = generatedClassName;
        this.generatedPackageName = generatedPackageName;
        this.objectClass = objectClass;
        this.fields = fields;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getGeneratedClassName() {
        return generatedClassName;
    }

    public void setGeneratedClassName(String generatedClassName) {
        this.generatedClassName = generatedClassName;
    }

    public String getGeneratedPackageName() {
        return generatedPackageName;
    }

    public void setGeneratedPackageName(String generatedPackageName) {
        this.generatedPackageName = generatedPackageName;
    }

    public ObjectClass getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(ObjectClass objectClass) {
        this.objectClass = objectClass;
    }

    public List<FieldMeta> getFields() {
        return fields;
    }

    public ClassMeta addField(FieldMeta field) {
        if (this.fields == null) {
            this.fields = new LinkedList<>();
        }
        this.fields.add(field);
        return this;
    }

    public void setFields(List<FieldMeta> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClassMeta classMeta)) return false;
        return Objects.equals(className, classMeta.className) && Objects.equals(packageName, classMeta.packageName) && Objects.equals(generatedClassName, classMeta.generatedClassName) && Objects.equals(generatedPackageName, classMeta.generatedPackageName) && Objects.equals(objectClass, classMeta.objectClass) && Objects.equals(fields, classMeta.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, packageName, generatedClassName, generatedPackageName, objectClass, fields);
    }

    @Override
    public String toString() {
        return "ClassMeta{" +
                "className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", generatedClassName='" + generatedClassName + '\'' +
                ", generatedPackageName='" + generatedPackageName + '\'' +
                ", objectClass=" + objectClass +
                ", fields=" + fields +
                '}';
    }
}
