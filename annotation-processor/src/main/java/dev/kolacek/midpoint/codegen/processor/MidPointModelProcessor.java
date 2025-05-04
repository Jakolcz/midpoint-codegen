package dev.kolacek.midpoint.codegen.processor;

import com.google.auto.service.AutoService;
import dev.kolacek.midpoint.codegen.annotation.ConnectorObject;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "dev.kolacek.midpoint.codegen.annotation.ConnectorObject"
})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
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
        Set<? extends Element> connectorObjectElements = roundEnv.getElementsAnnotatedWith(ConnectorObject.class);
        return false;
    }
}
