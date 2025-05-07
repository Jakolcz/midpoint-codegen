package dev.kolacek.midpoint.codegen.processor.generator;

import com.palantir.javapoet.*;
import dev.kolacek.midpoint.codegen.annotation.ConnectorObject;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class ConnectorObjectBuilderGenerator {

    private final Elements elementUtils;
    private final Filer filer;

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
        ClassName objectClassInfoUtilsClass = ClassName.get("dev.kolacek.midpoint.codegen.util", "ObjectClassInfoUtils");

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

            TypeMirror fieldType = element.asType();
            String fieldName = element.getSimpleName().toString();


        }

        MethodSpec objectClassInfoMethod = MethodSpec.methodBuilder("objectClassInfoBuilder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode(objectClassInfoBuilderMethod.build())
                .addStatement("return builder")
                .returns(objectClassInfoClass)
                .build();

        classBuilder.addMethod(objectClassInfoMethod);

        TypeSpec generatedType = classBuilder.build();
        // Create a JavaFile with the package and TypeSpec
        JavaFile javaFile = JavaFile.builder(packageName, generatedType)
                // You can customize import formatting if needed
                .skipJavaLangImports(true)
                .indent("    ") // 4 space indentation
                .build();

        System.out.println("Output class: " + javaFile);
    }
}
