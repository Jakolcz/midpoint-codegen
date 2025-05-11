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

package dev.kolacek.midpoint.codegen.processor.generator.exception;

import javax.lang.model.element.Element;

public class MissingGetterException extends RuntimeException {

    private final Element element;
    private final String expectedGetterName;

    public MissingGetterException(Element element, String expectedGetterName) {
        super("Missing getter " + expectedGetterName + " for element " + element);
        this.element = element;
        this.expectedGetterName = expectedGetterName;
    }

    public Element getElement() {
        return element;
    }

    public String getExpectedGetterName() {
        return expectedGetterName;
    }
}
