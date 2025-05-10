package dev.kolacek.midpoint.codegen.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;

public class BasicTest {

    @Test
    public void test() {
        JavaFileObject source = JavaFileObjects.forResource("SampleClass.java");

        Compilation compilation = Compiler.javac()
                .withProcessors(new MidPointModelProcessor())
                .compile(source);

        CompilationSubject.assertThat(compilation).succeeded();
        for (Diagnostic<? extends JavaFileObject> diagnostic : compilation.diagnostics()) {
            System.out.println("Diagnostic: [" + diagnostic.getKind() + "]: " + diagnostic.getMessage(null));
        }

        List<JavaFileObject> generatedFiles = compilation.generatedSourceFiles();
        Assertions.assertFalse(generatedFiles.isEmpty());
        for (JavaFileObject file : generatedFiles) {
            System.out.println("Generated file: " + file.getName());
        }

        List<JavaFileObject> sourceFiles = new ArrayList<>(generatedFiles.size() + 1);
        sourceFiles.add(source);
        sourceFiles.addAll(generatedFiles);

        Compilation generatedCompilation = Compiler.javac().compile(sourceFiles);
        CompilationSubject.assertThat(generatedCompilation).succeeded();
    }
}
