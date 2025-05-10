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
import dev.kolacek.midpoint.codegen.annotation.ConnectorAttribute;
import dev.kolacek.midpoint.codegen.annotation.ConnectorModel;
import dev.kolacek.midpoint.codegen.annotation.IgnoreAttribute;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;

import javax.annotation.Nullable;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConnectorObjectBuilderGenerator {

    /**
     * Set of supported basic classes for connector attributes.
     */
    private static final Set<Class<?>> SUPPORTED_BASIC_CLASSES = Set.of(String.class, long.class, Long.class, char.class,
            Character.class, double.class, Double.class, float.class, Float.class, int.class, Integer.class, boolean.class,
            Boolean.class, byte.class, Byte.class, byte[].class, BigDecimal.class, BigInteger.class, GuardedByteArray.class,
            GuardedString.class/*, Map.class, ZonedDateTime.class, Enum.class*/);
    private static final Set<String> SUPPORTED_BASIC_CLASSES_FQN = SUPPORTED_BASIC_CLASSES.stream().map(Class::getCanonicalName).collect(Collectors.toSet());

    private static final String PARAM_CONNECTOR_BUILDER = "data";
    private static final String BUILDER_NAME = "builder";

    private final Elements elementUtils;
    private final Filer filer;
    private final Messager messager;
    private final Types typeUtils;


    public ConnectorObjectBuilderGenerator(Elements elementUtils, Messager messager, Types typeUtils, Filer filer) {
        this.elementUtils = elementUtils;
        this.messager = messager;
        this.typeUtils = typeUtils;
        this.filer = filer;
    }

    public void generate(TypeElement classElement) throws IOException {
        String className = classElement.getSimpleName().toString();
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        String generatedClassName = className + "Builders";
        ConnectorModel annotation = classElement.getAnnotation(ConnectorModel.class);

        System.out.println("Generating class: " + generatedClassName + " in package: " + packageName);

        ClassName generatedClass = ClassName.get(packageName, generatedClassName);
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
                .addStatement("$L.setType($S)", BUILDER_NAME, annotation.objectClassType());

        CodeBlock.Builder connectorObjectBuilderMethod = CodeBlock.builder()
                .addStatement("$T $L = new $T()", connectorObjectBuilderClass, BUILDER_NAME, connectorObjectBuilderClass);
        // TODO Set object class type

        Map<String, ExecutableElement> getters = new HashMap<>();
        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }

            if (element.getAnnotation(IgnoreAttribute.class) != null) {
                continue;
            }

            VariableElement fieldElement = (VariableElement) element;
            ExecutableElement getter = findGetter(fieldElement, classElement);
            if (getter != null) {
                getters.put(fieldElement.getSimpleName().toString(), getter);
            }
        }


        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }

            if (element.getAnnotation(IgnoreAttribute.class) != null) {
                continue;
            }

            VariableElement fieldElement = (VariableElement) element;

            if (!isSupportedType(fieldElement.asType())) {
                warn(fieldElement, "Field %s of type %s is not supported", fieldElement.getSimpleName(), fieldElement.asType());
                continue;
            }

            FieldInfo fieldInfo = toFieldInfo(fieldElement);

            // TODO add Enum -> String mapping

            objectClassInfoBuilderMethod.addStatement("builder.addAttributeInfo(new $T().setName($S).setRequired($L).setType($T.class).setMultiValued($L).build())",
                    attributeInfoBuilderClass,
                    fieldInfo.name(),
                    fieldInfo.required(),
                    fieldInfo.type(),
                    fieldInfo.multiValued());

            ExecutableElement getter = getters.get(fieldElement.getSimpleName().toString());
            if (getter == null) {
                warn(fieldElement, "No getter found for field %s", fieldElement.getSimpleName());
                continue;
            }

            connectorObjectBuilderMethod.addStatement("$L.addAttribute($S, $L.$L())",
                    BUILDER_NAME,
                    fieldInfo.name(),
                    PARAM_CONNECTOR_BUILDER,
                    getter.getSimpleName());
        }

        objectClassInfoBuilderMethod.addStatement("return $L", BUILDER_NAME);
        connectorObjectBuilderMethod.addStatement("return $L", BUILDER_NAME);

        MethodSpec objectClassInfoMethod = createMethod("objectClassInfoBuilder", objectClassInfoBuilderClass, objectClassInfoBuilderMethod.build());
        MethodSpec connectorObjectMethod = createMethod("connectorObjectBuilder", connectorObjectBuilderClass, connectorObjectBuilderMethod.build(), ParameterSpec.builder(definingClass, PARAM_CONNECTOR_BUILDER).build());

        classBuilder.addMethod(objectClassInfoMethod);
        classBuilder.addMethod(connectorObjectMethod);

        TypeSpec generatedType = classBuilder.build();
        // Create a JavaFile with the package and TypeSpec
        JavaFile javaFile = JavaFile.builder(packageName, generatedType)
                .skipJavaLangImports(true)
                .indent("    ") // 4 space indentation
                .build();

        System.out.println("Output class: " + javaFile);
        javaFile.writeTo(filer);
    }

    private MethodSpec createMethod(String name, ClassName returnType, CodeBlock codeBlock) {
        return createMethod(name, returnType, codeBlock, null);
    }

    private MethodSpec createMethod(String name, ClassName returnType, CodeBlock codeBlock, ParameterSpec parameter) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnType)
                .addCode(codeBlock);
        if (parameter != null) {
            builder.addParameter(parameter);
        }
        return builder.build();
    }

    /**
     * Finds the getter method for a given field following Java Bean convention.
     * Returns null if no getter is found.
     */
    @Nullable
    private ExecutableElement findGetter(VariableElement fieldElement, TypeElement classElement) {
        String fieldName = fieldElement.getSimpleName().toString();
        String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        // Check if the field is boolean or not, booleans use "is" prefix
        boolean isBoolean = fieldElement.asType().getKind() == TypeKind.BOOLEAN;
        String getterPrefix = isBoolean ? "is" : "get";
        String expectedGetterName = getterPrefix + capitalizedFieldName;

        // Look for the getter among all methods
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (enclosedElement.getKind() != ElementKind.METHOD) {
                continue;
            }

            ExecutableElement methodElement = (ExecutableElement) enclosedElement;

            // Check if method name matches and it has no parameters
            if (methodElement.getSimpleName().toString().equals(expectedGetterName) &&
                    methodElement.getParameters().isEmpty()) {
                return methodElement;
            }
        }

        return null; // No getter found
    }

    private boolean isSupportedType(TypeMirror typeMirror) {
        TypeKind kind = typeMirror.getKind();
        // Support all primitive types
        if (kind.isPrimitive()) {
            return true;
        }

        TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
        ElementKind elementKind = typeElement.getKind();

        // ENUMs are not supported for now
        if (elementKind == ElementKind.ENUM) {
            return false;
        }

        if (elementKind == ElementKind.CLASS) {
            String className = typeElement.getQualifiedName().toString();

            return SUPPORTED_BASIC_CLASSES_FQN.contains(className);
        }

        return false;
    }

    private FieldInfo toFieldInfo(VariableElement fieldElement) {
        TypeMirror fieldType = fieldElement.asType();
        ConnectorAttribute connectorAttribute = fieldElement.getAnnotation(ConnectorAttribute.class);

        boolean isRequired;
        boolean isMultivalued;
        String fieldName;

        if (connectorAttribute != null) {
            isRequired = connectorAttribute.required();
            isMultivalued = connectorAttribute.multivalued();
            fieldName = connectorAttribute.value();
        } else {
            isRequired = ConnectorAttribute.DEFAULT_REQUIRED;
            isMultivalued = ConnectorAttribute.DEFAULT_MULTIVALUED;
            fieldName = fieldElement.getSimpleName().toString();
        }

        return new FieldInfo(fieldName, ClassName.get(fieldType), isRequired, isMultivalued);
    }

    private void warn(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args), e);
    }

    private record FieldInfo(String name, TypeName type, boolean required, boolean multiValued) {

    }
}
