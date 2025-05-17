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

package dev.kolacek.midpoint.codegen.processor.generator;

import com.palantir.javapoet.*;
import dev.kolacek.midpoint.codegen.processor.MessagingService;
import dev.kolacek.midpoint.codegen.processor.generator.meta.ClassMeta;
import dev.kolacek.midpoint.codegen.processor.generator.meta.FieldMeta;
import dev.kolacek.midpoint.codegen.processor.generator.meta.ObjectClassMeta;
import dev.kolacek.midpoint.codegen.processor.generator.util.ConnectorModelPreprocessor;
import dev.kolacek.midpoint.codegen.processor.generator.util.PoetUtil;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.Optional;

public class ConnectorObjectBuilderGenerator {

    public static final String PARAM_CONNECTOR_BUILDER = "data";
    public static final String BUILDER_NAME = "builder";

    private final Elements elementUtils;
    private final Filer filer;
    private final MessagingService messagingService;
    private final Types typeUtils;


    public ConnectorObjectBuilderGenerator(Elements elementUtils, MessagingService messagingService, Types typeUtils, Filer filer) {
        this.elementUtils = elementUtils;
        this.messagingService = messagingService;
        this.typeUtils = typeUtils;
        this.filer = filer;
    }

    public void generate(TypeElement classElement) throws IOException {
        ConnectorModelPreprocessor preprocessor = new ConnectorModelPreprocessor(elementUtils, typeUtils, messagingService);
        ClassMeta classMeta = preprocessor.prepareClassMeta(classElement);
        ObjectClassMeta objectClassMeta = classMeta.getObjectClassMeta();

        System.out.println("Generating class: " + classMeta.getClassName() + " in package: " + classMeta.getPackageName());

        ClassName generatedClass = ClassName.get(classMeta.getPackageName(), classMeta.getGeneratedClassName());
        ClassName definingClass = ClassName.get(classElement);

        // Import necessary classes from MidPoint/ConnId
        ClassName objectClassInfoBuilderClass = ClassName.get("org.identityconnectors.framework.common.objects", "ObjectClassInfoBuilder");
        ClassName attributeInfoBuilderClass = ClassName.get("org.identityconnectors.framework.common.objects", "AttributeInfoBuilder");
        ClassName connectorObjectBuilderClass = ClassName.get("org.identityconnectors.framework.common.objects", "ConnectorObjectBuilder");

        // Create the builder class
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClass)
                .addModifiers(Modifier.PUBLIC);

        CodeBlock.Builder objectClassInfoBuilderMethod = CodeBlock.builder()
                .addStatement("$T $L = new $T()", objectClassInfoBuilderClass, BUILDER_NAME, objectClassInfoBuilderClass)
                .addStatement("$L.setType($L)", BUILDER_NAME, objectClassMeta.getObjectClassTypeCodeBlock());

        CodeBlock.Builder connectorObjectBuilderMethod = CodeBlock.builder()
                .addStatement("$T $L = new $T()", connectorObjectBuilderClass, BUILDER_NAME, connectorObjectBuilderClass)
                .addStatement("$L.setObjectClass($L)", BUILDER_NAME, objectClassMeta.getConnectorObjectBuilderObjectClassCodeBlock());

        for (FieldMeta fieldMeta : classMeta.getFields()) {
            objectClassInfoBuilderMethod.addStatement("builder.addAttributeInfo(new $T().setName($S).setRequired($L).setType($T.class).setMultiValued($L).build())",
                    attributeInfoBuilderClass,
                    fieldMeta.getName(),
                    fieldMeta.isRequired(),
                    fieldMeta.getFieldType(),
                    fieldMeta.isMultivalued());

            Optional<ExecutableElement> getter = fieldMeta.getGetter();
            if (getter.isEmpty()) {
                continue;
            }

            if (fieldMeta.getEnumMeta().isPresent()) {
                connectorObjectBuilderMethod.add(PoetUtil.addEnumAttributeBlock(fieldMeta, getter.get()));
            } else {
                connectorObjectBuilderMethod.add(PoetUtil.addAttributeBlock(fieldMeta, getter.get()));
            }
        }

        objectClassInfoBuilderMethod.addStatement("return $L", BUILDER_NAME);
        connectorObjectBuilderMethod.addStatement("return $L", BUILDER_NAME);

        MethodSpec objectClassInfoMethod = PoetUtil.createMethod("objectClassInfoBuilder", objectClassInfoBuilderClass, objectClassInfoBuilderMethod.build());
        MethodSpec connectorObjectMethod = PoetUtil.createMethod("connectorObjectBuilder", connectorObjectBuilderClass, connectorObjectBuilderMethod.build(), ParameterSpec.builder(definingClass, PARAM_CONNECTOR_BUILDER).build());

        classBuilder.addMethod(objectClassInfoMethod);
        classBuilder.addMethod(connectorObjectMethod);

        TypeSpec generatedType = classBuilder.build();
        // Create a JavaFile with the package and TypeSpec
        JavaFile javaFile = JavaFile.builder(classMeta.getPackageName(), generatedType)
                .skipJavaLangImports(true)
                .indent("    ") // 4 space indentation
                .build();

        System.out.println("Output class: " + javaFile);
        javaFile.writeTo(filer);
    }
}