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

package dev.kolacek.midpoint.codegen.processor.generator.util;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import dev.kolacek.midpoint.codegen.processor.generator.ConnectorObjectBuilderGenerator;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

public final class PoetUtil {
    private PoetUtil() {
    }

    public static CodeBlock addEnumAttributeBlock(ConnectorObjectBuilderGenerator.FieldInfo fieldInfo, ExecutableElement getter) {
        return CodeBlock.builder()
                .beginControlFlow("if ($L.$L() != null)", ConnectorObjectBuilderGenerator.PARAM_CONNECTOR_BUILDER, getter.getSimpleName())
                .addStatement("$L.addAttribute($S, $L.$L().$L())",
                        ConnectorObjectBuilderGenerator.BUILDER_NAME,
                        fieldInfo.name(),
                        ConnectorObjectBuilderGenerator.PARAM_CONNECTOR_BUILDER,
                        getter.getSimpleName(),
                        fieldInfo.enumToString())
                .endControlFlow()
                .build();
    }

    public static CodeBlock addAttributeBlock(ConnectorObjectBuilderGenerator.FieldInfo fieldInfo, ExecutableElement getter) {
        return CodeBlock.builder()
                .addStatement("$L.addAttribute($S, $L.$L())",
                        ConnectorObjectBuilderGenerator.BUILDER_NAME,
                        fieldInfo.name(),
                        ConnectorObjectBuilderGenerator.PARAM_CONNECTOR_BUILDER,
                        getter.getSimpleName())
                .build();
    }

    public static MethodSpec createMethod(String name, ClassName returnType, CodeBlock codeBlock) {
        return createMethod(name, returnType, codeBlock, null);
    }

    public static MethodSpec createMethod(String name, ClassName returnType, CodeBlock codeBlock, ParameterSpec parameter) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnType)
                .addCode(codeBlock);
        if (parameter != null) {
            builder.addParameter(parameter);
        }
        return builder.build();
    }
}