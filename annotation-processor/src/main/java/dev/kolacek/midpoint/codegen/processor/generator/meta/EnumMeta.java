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

import java.util.Objects;

public class EnumMeta {

    private String toStringMethod;

    public String getToStringMethod() {
        return toStringMethod;
    }

    public void setToStringMethod(String toStringMethod) {
        this.toStringMethod = toStringMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EnumMeta enumMeta)) return false;
        return Objects.equals(toStringMethod, enumMeta.toStringMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toStringMethod);
    }

    @Override
    public String toString() {
        return "EnumMeta{" +
                "toStringMethod='" + toStringMethod + '\'' +
                '}';
    }
}
