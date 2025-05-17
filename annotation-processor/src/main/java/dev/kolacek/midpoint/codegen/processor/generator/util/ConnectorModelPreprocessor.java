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

import com.palantir.javapoet.TypeName;
import dev.kolacek.midpoint.codegen.annotation.*;
import dev.kolacek.midpoint.codegen.config.AnnotationDefaults;
import dev.kolacek.midpoint.codegen.config.ReportingPolicy;
import dev.kolacek.midpoint.codegen.processor.MessagingService;
import dev.kolacek.midpoint.codegen.processor.generator.exception.MissingGetterException;
import dev.kolacek.midpoint.codegen.processor.generator.meta.ClassMeta;
import dev.kolacek.midpoint.codegen.processor.generator.meta.EnumMeta;
import dev.kolacek.midpoint.codegen.processor.generator.meta.FieldMeta;
import dev.kolacek.midpoint.codegen.processor.generator.meta.ObjectClassMeta;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.ObjectClass;

import javax.annotation.Nullable;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConnectorModelPreprocessor {

    /**
     * Set of supported basic classes for connector attributes.
     */
    private static final Set<Class<?>> SUPPORTED_BASIC_CLASSES = Set.of(String.class, long.class, Long.class, char.class,
            Character.class, double.class, Double.class, float.class, Float.class, int.class, Integer.class, boolean.class,
            Boolean.class, byte.class, Byte.class, byte[].class, BigDecimal.class, BigInteger.class, GuardedByteArray.class,
            GuardedString.class, /*Map.class, ZonedDateTime.class,*/ Enum.class);
    private static final Set<Class<?>> SUPPORTED_COLLECTION_CLASSES = Set.of(List.class, Set.class);

    private static final Set<String> SUPPORTED_BASIC_CLASSES_FQN = SUPPORTED_BASIC_CLASSES.stream().map(Class::getCanonicalName).collect(Collectors.toSet());
    private static final Set<String> SUPPORTED_COLLECTION_CLASSES_FQN = SUPPORTED_COLLECTION_CLASSES.stream().map(Class::getCanonicalName).collect(Collectors.toSet());

    private static final Map<String, ObjectClassMeta> OBJECT_CLASS_MAP = Map.of(
            ObjectClass.ACCOUNT_NAME, ObjectClassMeta.ACCOUNT,
            ObjectClass.GROUP_NAME, ObjectClassMeta.GROUP,
            ObjectClass.ALL_NAME, ObjectClassMeta.ALL
    );

    private final Elements elementUtils;
    private final Types typeUtils;
    private final MessagingService messagingService;

    public ConnectorModelPreprocessor(Elements elementUtils, Types typeUtils, MessagingService messagingService) {
        this.elementUtils = elementUtils;
        this.typeUtils = typeUtils;
        this.messagingService = messagingService;
    }

    public ClassMeta prepareClassMeta(TypeElement classElement) {
        // This is the annotation we can be sure is present
        ClassMeta classMeta = fromClassElement(classElement);

        List<FieldMeta> fieldMetas = prepareFieldMetas(classElement);

        validateUidAndNameFields(fieldMetas, classElement);
        classMeta.setFields(fieldMetas);

        return classMeta;
    }

    public List<FieldMeta> prepareFieldMetas(TypeElement classElement) {
        ReportingPolicy reportingPolicy = classElement.getAnnotation(ConnectorModel.class).missingGetterPolicy();
        List<FieldMeta> fieldMetas = new LinkedList<>();

        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }

            if (element.getAnnotation(IgnoreAttribute.class) != null) {
                continue;
            }

            VariableElement fieldElement = (VariableElement) element;
            FieldMeta fieldMeta = prepareFieldMeta(fieldElement, reportingPolicy);
            fieldMetas.add(fieldMeta);
        }

        return fieldMetas;
    }

    public FieldMeta prepareFieldMeta(VariableElement fieldElement, ReportingPolicy reportingPolicy) {
        FieldMeta fieldMeta = basicFieldMetaFromAnnotation(fieldElement);
        ExecutableElement getterElement = findGetter(fieldMeta.getGetterName(), fieldElement);

        if (getterElement == null) {
            if (reportingPolicy == ReportingPolicy.ERROR) {
                throw new MissingGetterException(fieldElement, fieldMeta.getGetterName());
            } else if (reportingPolicy == ReportingPolicy.WARNING) {
                // Log a warning
                messagingService.warn(fieldElement, "Missing getter method for field '%s'. Expected getter name: '%s'.", fieldMeta.getName(), fieldMeta.getGetterName());
            }
        } else {
            fieldMeta.setGetter(getterElement);
        }

        // type, multival a enumek
        handleTypeInfo(fieldMeta, fieldElement);
        fieldMeta.setUidField(fieldElement.getAnnotation(UidField.class) != null);
        fieldMeta.setNameField(fieldElement.getAnnotation(NameField.class) != null);

        return fieldMeta;
    }


    private void handleTypeInfo(FieldMeta fieldMeta, VariableElement fieldElement) {
        TypeMirror fieldType = fieldElement.asType();
        TypeKind fieldTypeKind = fieldType.getKind();

        if (fieldTypeKind.isPrimitive()) {
            // primitive needs special handling
            fieldMeta.setMultivalued(false);
            fieldMeta.setFieldType(TypeName.get(fieldType));
        } else if (fieldTypeKind == TypeKind.ARRAY) {
            // get the original type
            TypeMirror componentType = ((ArrayType) fieldType).getComponentType();
            fieldMeta.setMultivalued(true);
            fieldMeta.setFieldType(TypeName.get(componentType));
        } else {
            TypeElement typeElement = (TypeElement) typeUtils.asElement(fieldType);
            ElementKind elementKind = typeElement.getKind();
            if (elementKind == ElementKind.ENUM) {
                fieldMeta.setFieldType(TypeName.get(String.class));
                fieldMeta.setMultivalued(false);
                fieldMeta.setEnumMeta(new EnumMeta(AnnotationUtil.getEnumToString(fieldElement.getAnnotation(EnumAttribute.class))));
            } else if (SUPPORTED_COLLECTION_CLASSES_FQN.contains(typeElement.getQualifiedName().toString())) {
                fieldMeta.setMultivalued(true);

                if (fieldType instanceof DeclaredType declaredType) {
                    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                    if (!typeArguments.isEmpty()) {
                        // Use the first type argument
                        TypeMirror elementType = typeArguments.get(0);
                        fieldMeta.setFieldType(TypeName.get(elementType));
                    } else {
                        // TODO needs better handling, probably conversion to String?
                        fieldMeta.setFieldType(TypeName.get(Object.class));
                    }
                } else {
                    // Fallback
                    fieldMeta.setFieldType(TypeName.get(Object.class));
                }
            } else {
                fieldMeta.setFieldType(TypeName.get(fieldType));
            }
        }
    }

    @Nullable
    private ExecutableElement findGetter(String expectedGetterName, VariableElement fieldElement) {
        // Get the enclosing class and then its elements
        for (Element enclosedElement : fieldElement.getEnclosingElement().getEnclosedElements()) {
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

        return null;
    }

    private void validateUidAndNameFields(List<FieldMeta> fieldMetas, TypeElement classElement) {
        long uidCount = fieldMetas.stream().filter(FieldMeta::isUidField).count();
        long nameCount = fieldMetas.stream().filter(FieldMeta::isNameField).count();

        if (uidCount != 1) {
            messagingService.error(classElement, "Each connector model must have exactly one field annotated with @UidField, found %d", uidCount);
        }

        if (nameCount != 1) {
            messagingService.error(classElement, "Each connector model must have exactly one field annotated with @NameField, found %d", nameCount);
        }
    }

    /**
     * Creates a basic FieldMeta from the (possibly missing) ConnectorAttribute annotation.
     * <p>
     * The resulting FieldMeta will have the following properties:
     * <ul>
     *     <li>name: the name of the field, either from the annotation or the field name</li>
     *     <li>getterName: the name of the getter method, either from the annotation or generated from the field name</li>
     *     <li>required: true if the field is annotated with ConnectorAttribute and required is true, false otherwise</li>
     * </ul>
     * </p>
     *
     * @param element the field element to create the FieldMeta from
     * @return a FieldMeta object with the name, getterName, and required properties set
     */
    private FieldMeta basicFieldMetaFromAnnotation(VariableElement element) {
        ConnectorAttribute annotation = element.getAnnotation(ConnectorAttribute.class);

        String fieldName;
        boolean required;
        String getterName;
        if (annotation == null) {
            fieldName = element.getSimpleName().toString();
            required = AnnotationDefaults.ConnectorAttribute.DEFAULT_REQUIRED;
            getterName = getGetterName(element);
        } else {
            fieldName = annotation.value().isBlank() ? element.getSimpleName().toString() : annotation.value();
            required = annotation.required();
            getterName = annotation.getterName().isBlank() ? getGetterName(element) : annotation.getterName();
        }

        return new FieldMeta(fieldName, getterName, required);
    }

    private String getGetterName(VariableElement element) {
        String fieldName = element.getSimpleName().toString();
        String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        // Check if the field is boolean or not, booleans use "is" prefix
        boolean isBoolean = element.asType().getKind() == TypeKind.BOOLEAN;
        String getterPrefix = isBoolean ? "is" : "get";

        return getterPrefix + capitalizedFieldName;
    }

    private ClassMeta fromClassElement(TypeElement classElement) {
        ConnectorModel annotation = classElement.getAnnotation(ConnectorModel.class);
        ClassMeta classMeta = new ClassMeta();

        classMeta.setClassName(classElement.getSimpleName().toString());
        classMeta.setPackageName(elementUtils.getPackageOf(classElement).getQualifiedName().toString());

        String generatedClassNameSuffix = annotation.suffix().isBlank() ? AnnotationDefaults.ConnectorModel.SUFFIX : annotation.suffix();
        classMeta.setGeneratedClassName(classMeta.getClassName() + generatedClassNameSuffix);

        String generatedPackageName = annotation.packageName().isBlank() ? classMeta.getPackageName() : annotation.packageName();
        classMeta.setGeneratedPackageName(generatedPackageName);
        classMeta.setObjectClassMeta(OBJECT_CLASS_MAP.getOrDefault(annotation.objectClassType(), new ObjectClassMeta(annotation.objectClassType())));

        return classMeta;
    }

}
