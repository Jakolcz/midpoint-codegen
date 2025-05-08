package dev.kolacek.midpoint.codegen.processor.generator;

import com.palantir.javapoet.*;
import dev.kolacek.midpoint.codegen.annotation.ConnectorAttribute;
import dev.kolacek.midpoint.codegen.annotation.ConnectorObject;
import dev.kolacek.midpoint.codegen.annotation.IgnoreAttribute;
import dev.kolacek.midpoint.codegen.util.ObjectClassInfoBuilderUtil;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

public class ConnectorObjectBuilderGenerator {

    private final Elements elementUtils;
    private final Filer filer;

    private static final Set<Class<?>> SUPPORTED_BASIC_CLASSES = Set.of(String.class, long.class, Long.class, char.class,
            Character.class, double.class, Double.class, float.class, Float.class, int.class, Integer.class, boolean.class,
            Boolean.class, byte.class, Byte.class, byte[].class, BigDecimal.class, BigInteger.class, GuardedByteArray.class,
            GuardedString.class, Map.class, ZonedDateTime.class, Enum.class);

    public ConnectorObjectBuilderGenerator(Elements elementUtils, Filer filer) {
        this.elementUtils = elementUtils;
        this.filer = filer;
    }

    public void generate(TypeElement classElement) {
        String className = classElement.getSimpleName().toString();
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        String generatedClassName = className + "Builders";
        ConnectorObject annotation = classElement.getAnnotation(ConnectorObject.class);

        System.out.println("Generating class: " + generatedClassName + " in package: " + packageName);

        ClassName generatedClass = ClassName.get(packageName, generatedClassName);

        // Import necessary classes from MidPoint/ConnId
        ClassName objectClassInfoBuilderClass = ClassName.get("org.identityconnectors.framework.common.objects", "ObjectClassInfoBuilder");
        ClassName objectClassInfoClass = ClassName.get("org.identityconnectors.framework.common.objects", "ObjectClassInfo");
        ClassName attributeInfoBuilderClass = ClassName.get("org.identityconnectors.framework.common.objects", "AttributeInfoBuilder");

        // Create the builder class
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClass)
                .addModifiers(Modifier.PUBLIC);

        CodeBlock.Builder objectClassInfoBuilderMethod = CodeBlock.builder()
                .addStatement("$T builder = new $T()", objectClassInfoBuilderClass, objectClassInfoBuilderClass)
                .addStatement("builder.setType($S)", annotation.objectClassType());

        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() != ElementKind.FIELD) {
                continue;
            }

            if (element.getAnnotation(IgnoreAttribute.class) != null) {
                continue;
            }

            VariableElement fieldElement = (VariableElement) element;

            FieldInfo fieldInfo = toFieldInfo(fieldElement);

            objectClassInfoBuilderMethod.addStatement("builder.addAttributeInfo(new $T().setName($S).setRequired($L).setType($T.class).setMultiValued($L).build())",
                    attributeInfoBuilderClass,
                    fieldInfo.name(),
                    fieldInfo.required(),
                    fieldInfo.type(),
                    fieldInfo.multiValued());
        }

        MethodSpec objectClassInfoMethod = MethodSpec.methodBuilder("objectClassInfoBuilder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode(objectClassInfoBuilderMethod.build())
                .addStatement("return builder")
                .returns(objectClassInfoBuilderClass)
                .build();

        classBuilder.addMethod(objectClassInfoMethod);

        TypeSpec generatedType = classBuilder.build();
        // Create a JavaFile with the package and TypeSpec
        JavaFile javaFile = JavaFile.builder(packageName, generatedType)
                .skipJavaLangImports(true)
                .indent("    ") // 4 space indentation
                .build();

        System.out.println("Output class: " + javaFile);
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

    private record FieldInfo(String name, TypeName type, boolean required, boolean multiValued) {

    }
}
