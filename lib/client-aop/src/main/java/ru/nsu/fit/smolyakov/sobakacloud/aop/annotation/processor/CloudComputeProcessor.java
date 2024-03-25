package ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.stream.Stream;


@SupportedAnnotationTypes({
    "ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaCloudCompute",
    "ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaEntryMethod"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CloudComputeProcessor extends AbstractProcessor {
    public static final String DEFAULT_VALUE = "";

    private Element findElementByName(
        Stream<? extends Element> stream,
        String name,
        String onErrorMsg
    ) {
        var maybeAnnotation = stream
            .filter(typeElement -> typeElement.getSimpleName().toString().equals(name))
            .findAny();

        if (maybeAnnotation.isEmpty()) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                onErrorMsg
            );
            return null;
        }
        return maybeAnnotation.get();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() != 2) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "something wrong with annotations set: " + annotations.size() + " items instead of 2"
            );
            return false;
        }


        var computeClassAnnotationElement =
            (TypeElement) findElementByName(
                annotations.stream(),
                "SobakaCloudCompute",
                "no @SobakaCloudCompute annotation"
                );
        if (computeClassAnnotationElement == null) return false;

        var serverAddressElement =
            (ExecutableElement) findElementByName(
                computeClassAnnotationElement.getEnclosedElements().stream(),
                "server",
                "no server() in @SobakaCloudCompute"
            );
        if (serverAddressElement == null) return false;

        var targetClassNameElement =
            (ExecutableElement) findElementByName(
                computeClassAnnotationElement.getEnclosedElements().stream(),
                "targetName",
                "no targetName() in @SobakaCloudCompute"
            );
        if (targetClassNameElement == null) return false;




        var entryMethodAnnotationElement =
            (TypeElement) findElementByName(
                annotations.stream(),
                "SobakaEntryMethod",
                "no @SobakaEntryMethod annotation"
            );
        if (entryMethodAnnotationElement == null) return false;

        var targetMethodNameElement =
            (ExecutableElement) findElementByName(
                computeClassAnnotationElement.getEnclosedElements().stream(),
                "targetName",
                "no targetName() in @SobakaEntryMethod"
            );
        if (targetMethodNameElement == null) return false;


        for (var element : roundEnv.getElementsAnnotatedWith(computeClassAnnotationElement)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@SobakaCloudCompute is for classes only"
                );
                return false;
            }

            var classAnnotationMirrorList = element.getAnnotationMirrors()
                .stream()
                .filter(mirror -> mirror.getAnnotationType().equals(computeClassAnnotationElement.asType()))
                .toList();

            if (classAnnotationMirrorList.size() != 1) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "ONE and exactly ONE class may be annotated with @SobakaCloudCompute"
                );
                return false;
            }
            //"ONE and exactly ONE static method should be annotated with @SobakaEntryMethod"

            var classAnnotationMirror = classAnnotationMirrorList.get(0);
            System.err.println(classAnnotationMirror.getElementValues());
        }
        return true;
    }
}
