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

import com.google.auto.service.AutoService;
import dev.kolacek.midpoint.codegen.annotation.ConnectorModel;
import dev.kolacek.midpoint.codegen.processor.generator.ConnectorObjectBuilderGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "dev.kolacek.midpoint.codegen.annotation.ConnectorObject"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class MidPointModelProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ConnectorObjectBuilderGenerator generator = new ConnectorObjectBuilderGenerator(elementUtils, messager, typeUtils, filer);
        for (Element element : roundEnv.getElementsAnnotatedWith(ConnectorModel.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                error(element, "@ConnectorObject can only be applied to classes.");
                continue;
            }

            TypeElement classElement = (TypeElement) element;
            try {
                generator.generate(classElement);
            } catch (IOException e) {
                error(classElement, "Failed to generate code for %s: %s", classElement.getQualifiedName(), e.getMessage());
            }
        }
        return true;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void warn(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
    }

    private void log(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e);
    }
}
