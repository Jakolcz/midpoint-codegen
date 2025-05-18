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

package dev.kolacek.midpoint.codegen.processor;

import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class MessagingService {

    private final Messager messager;

    public MessagingService(Messager messager) {
        this.messager = messager;
    }

    public void error(@Nullable Element e, String msg, Object... args) {
        this.log(Diagnostic.Kind.ERROR, e, msg, args);
    }

    public void warn(@Nullable Element e, String msg, Object... args) {
        this.log(Diagnostic.Kind.WARNING, e, msg, args);
    }

    public void note(@Nullable Element e, String msg, Object... args) {
        this.log(Diagnostic.Kind.NOTE, e, msg, args);
    }

    public void log(Diagnostic.Kind kind, @Nullable Element e, String msg, Object... args) {
        messager.printMessage(kind, String.format(msg, args), e);
    }
}
