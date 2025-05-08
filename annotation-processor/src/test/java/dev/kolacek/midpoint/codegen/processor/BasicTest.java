package dev.kolacek.midpoint.codegen.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class BasicTest {

    @Test
    public void test() {
        JavaFileObject source = JavaFileObjects.forResource("SampleClass.java");

        Compilation compilation = Compiler.javac()
                .withProcessors(new MidPointModelProcessor())
                .compile(source);

        for (Diagnostic<? extends JavaFileObject> diagnostic : compilation.diagnostics()) {
            System.out.println("Diagnostic: [" + diagnostic.getKind() + "]: " + diagnostic.getMessage(null));
        }
        CompilationSubject.assertThat(compilation).succeeded();
    }
}
