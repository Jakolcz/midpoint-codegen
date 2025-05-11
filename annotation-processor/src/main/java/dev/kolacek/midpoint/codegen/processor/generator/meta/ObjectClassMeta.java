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

import com.palantir.javapoet.CodeBlock;
import org.identityconnectors.framework.common.objects.ObjectClass;

import java.util.Objects;

public class ObjectClassMeta {

    public static ObjectClassMeta ACCOUNT = new ObjectClassMeta(true, "ACCOUNT");
    public static ObjectClassMeta GROUP = new ObjectClassMeta(true, "GROUP");
    public static ObjectClassMeta ALL = new ObjectClassMeta(true, "ALL");

    private boolean fromConnId;
    private String objectClassName;

    public ObjectClassMeta(String objectClassName) {
        this(false, objectClassName);
    }

    public ObjectClassMeta(boolean fromConnId, String objectClassName) {
        this.fromConnId = fromConnId;
        this.objectClassName = objectClassName;
    }

    public boolean isFromConnId() {
        return fromConnId;
    }

    public void setFromConnId(boolean fromConnId) {
        this.fromConnId = fromConnId;
    }

    public String getObjectClassName() {
        return objectClassName;
    }

    public void setObjectClassName(String objectClassName) {
        this.objectClassName = objectClassName;
    }

    public CodeBlock getObjectClassCodeBlock() {
        CodeBlock.Builder builder = CodeBlock.builder();

        if (fromConnId) {
            builder.addStatement("$T.$L", ObjectClass.class, objectClassName);
        } else {
            builder.addStatement("new $T($S)", ObjectClass.class, objectClassName);
        }

        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjectClassMeta that)) return false;
        return fromConnId == that.fromConnId && Objects.equals(objectClassName, that.objectClassName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromConnId, objectClassName);
    }

    @Override
    public String toString() {
        return "ObjectClassMeta{" +
                "fromConnId=" + fromConnId +
                ", objectClassName='" + objectClassName + '\'' +
                '}';
    }
}
