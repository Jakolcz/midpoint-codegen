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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ClassMeta {

    private String className;
    private String packageName;
    private String generatedClassName;
    private String generatedPackageName;
    private ObjectClassMeta objectClassMeta;
    private List<FieldMeta> fields;

    public ClassMeta() {
    }

    public ClassMeta(String className, String packageName, String generatedClassName, String generatedPackageName, ObjectClassMeta objectClassMeta, List<FieldMeta> fields) {
        this.className = className;
        this.packageName = packageName;
        this.generatedClassName = generatedClassName;
        this.generatedPackageName = generatedPackageName;
        this.objectClassMeta = objectClassMeta;
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

    public ObjectClassMeta getObjectClassMeta() {
        return objectClassMeta;
    }

    public void setObjectClassMeta(ObjectClassMeta objectClassMeta) {
        this.objectClassMeta = objectClassMeta;
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
        return Objects.equals(className, classMeta.className) && Objects.equals(packageName, classMeta.packageName) && Objects.equals(generatedClassName, classMeta.generatedClassName) && Objects.equals(generatedPackageName, classMeta.generatedPackageName) && Objects.equals(objectClassMeta, classMeta.objectClassMeta) && Objects.equals(fields, classMeta.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, packageName, generatedClassName, generatedPackageName, objectClassMeta, fields);
    }

    @Override
    public String toString() {
        return "ClassMeta{" +
                "className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", generatedClassName='" + generatedClassName + '\'' +
                ", generatedPackageName='" + generatedPackageName + '\'' +
                ", objectClassMeta=" + objectClassMeta +
                ", fields=" + fields +
                '}';
    }
}
