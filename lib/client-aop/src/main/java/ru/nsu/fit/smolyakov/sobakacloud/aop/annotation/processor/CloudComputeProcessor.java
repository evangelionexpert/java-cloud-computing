package ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


@SupportedAnnotationTypes({
    "ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaCloudCompute",
    "ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaEntryMethod"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CloudComputeProcessor extends AbstractProcessor {
    public static final String EMPTY_VALUE = "";
    public static final String DEFAULT_CLASS_SUFFIX = "SobakaCloud";

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

    private record AllElements(
        TypeElement computeClassAnnotationElement,
        ExecutableElement serverAddressElement,
        ExecutableElement targetShortClassNameElement,
        ExecutableElement targetPackageElement,

        TypeElement entryMethodAnnotationElement,
        ExecutableElement targetEntryMethodNameElement,
        ExecutableElement sleepBeforePollingMillisElement,
        ExecutableElement pollingIntervalMillisElement
    ) {}

    private AllElements parseAllElements(Set<? extends TypeElement> annotations) {
        TypeElement computeClassAnnotationElement =
            (TypeElement) findElementByName(
                annotations.stream(),
                "SobakaCloudCompute",
                "no @SobakaCloudCompute annotation"
            );
        if (computeClassAnnotationElement == null) return null;

        ExecutableElement serverAddressElement =
            (ExecutableElement) findElementByName(
                computeClassAnnotationElement.getEnclosedElements().stream(),
                "server",
                "no server() in @SobakaCloudCompute"
            );
        if (serverAddressElement == null) return null;

        ExecutableElement targetShortClassNameElement =
            (ExecutableElement) findElementByName(
                computeClassAnnotationElement
                    .getEnclosedElements()
                    .stream(),
                "targetShortClassName",
                "no targetShortClassName() in @SobakaCloudCompute"
            );
        if (targetShortClassNameElement == null) return null;

        ExecutableElement targetPackageElement =
            (ExecutableElement) findElementByName(
                computeClassAnnotationElement
                    .getEnclosedElements()
                    .stream(),
                "targetPackage",
                "no targetPackage() in @SobakaCloudCompute"
            );
        if (targetPackageElement == null) return null;


        TypeElement entryMethodAnnotationElement =
            (TypeElement) findElementByName(
                annotations.stream(),
                "SobakaEntryMethod",
                "no @SobakaEntryMethod annotation"
            );
        if (entryMethodAnnotationElement == null) return null;

        ExecutableElement targetMethodNameElement =
            (ExecutableElement) findElementByName(
                entryMethodAnnotationElement.getEnclosedElements().stream(),
                "targetName",
                "no targetName() in @SobakaEntryMethod"
            );
        if (targetMethodNameElement == null) return null;

        ExecutableElement sleepBeforePollingMillisElement =
            (ExecutableElement) findElementByName(
                entryMethodAnnotationElement.getEnclosedElements().stream(),
                "sleepBeforePollingMillis",
                "no sleepBeforePollingMillis() in @SobakaEntryMethod"
            );
        if (sleepBeforePollingMillisElement == null) return null;

        ExecutableElement pollingIntervalMillisElement =
            (ExecutableElement) findElementByName(
                entryMethodAnnotationElement.getEnclosedElements().stream(),
                "pollingIntervalMillis",
                "no pollingIntervalMillis() in @SobakaEntryMethod"
            );
        if (pollingIntervalMillisElement == null) return null;

        return new AllElements(
            computeClassAnnotationElement,
            serverAddressElement,
            targetShortClassNameElement,
            targetPackageElement,

            entryMethodAnnotationElement,
            targetMethodNameElement,
            sleepBeforePollingMillisElement,
            pollingIntervalMillisElement
        );
    }

    private record AnnotatedMethod(
        AnnotationMirror annotationMirror,
        ExecutableElement element
    ) {}

    private AnnotatedMethod findAnnotatedMethod(TypeElement classElement, TypeElement entryMethodAnnotationElement) {
        AnnotationMirror annotatedMethodAnnotationMirror = null;
        ExecutableElement annotatedMethodElement = null;

        for (var possiblyMethodElement : classElement.getEnclosedElements()) {
            ExecutableElement methodElement = (ExecutableElement) possiblyMethodElement;

            var optionalMethodAnnotationMirror = methodElement.getAnnotationMirrors()
                .stream()
                .filter(annotationMirror -> annotationMirror.getAnnotationType().equals(entryMethodAnnotationElement.asType()))
                .findAny();

            if (optionalMethodAnnotationMirror.isPresent()) {
                if (annotatedMethodElement != null) {
                    processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "ONE and exactly ONE method may be annotated with @SobakaEntryMethod"
                    );
                    return null;
                }

                annotatedMethodElement = methodElement;
                annotatedMethodAnnotationMirror = optionalMethodAnnotationMirror.get();
            }
        }

        if (annotatedMethodElement == null) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "ONE and exactly ONE method may be annotated with @SobakaEntryMethod"
            );
            return null;
        }

        return new AnnotatedMethod(annotatedMethodAnnotationMirror, annotatedMethodElement);
    }

    private record Param(
        String paramType,
        String paramName
    ) {}

    private record ClassInfoStrings(
        String returnType,
        String sourcePackage,
        String sourceShortClassName,
        String targetPackage,
        String targetShortClassName,
        String sourceEntryMethodName,
        String targetEntryMethodName,
        List<Param> params,
        int sleepBeforePollingMillis,
        int pollingIntervalMillis
    ) {}

    private Object getElementValueFromAnnotationMirror(AnnotationMirror annotationMirror, ExecutableElement e) {
        AnnotationValue value = annotationMirror.getElementValues().get(e);
        if (value == null) {
            value = e.getDefaultValue();
        }
        return value.getValue();
    }

    private ClassInfoStrings parseClass(TypeElement classElement, AllElements e) {
        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "Class annotated with @SobakaCloudCompute must be public"
            );
            return null;
        }

        AnnotationMirror classAnnotationMirror = classElement.getAnnotationMirrors()
            .stream()
            .filter(annotationMirror -> annotationMirror.getAnnotationType().equals(e.computeClassAnnotationElement.asType()))
            .findAny()
            .get(); // todo вернуть проверку?? вроде не нужно, тут и так всегда да

        AnnotatedMethod method = this.findAnnotatedMethod(classElement, e.entryMethodAnnotationElement);
        if (method == null) return null;


        // parsing
        String returnType = method.element.getReturnType().toString();
        String sourcePackage = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
        String sourceShortClassName = classElement.getSimpleName().toString();
        String targetPackage = (String) getElementValueFromAnnotationMirror(classAnnotationMirror, e.targetPackageElement);
        String targetShortClassName = (String) getElementValueFromAnnotationMirror(classAnnotationMirror, e.targetShortClassNameElement);
        String sourceEntryMethodName = method.element.getSimpleName().toString();
        String targetEntryMethodName = (String) getElementValueFromAnnotationMirror(method.annotationMirror, e.targetEntryMethodNameElement);
        int sleepBeforePollingMillis = (int) getElementValueFromAnnotationMirror(method.annotationMirror, e.sleepBeforePollingMillisElement);
        int pollingIntervalMillis = (int) getElementValueFromAnnotationMirror(method.annotationMirror, e.pollingIntervalMillisElement);

        List<Param> paramsList = method.element.getParameters().stream()
            .map(param ->
                new Param(
                    param.asType().toString(),
                    param.getSimpleName().toString()
                    )
            ).toList();

        return new ClassInfoStrings(
            returnType,
            sourcePackage,
            sourceShortClassName,
            targetPackage,
            targetShortClassName,
            sourceEntryMethodName,
            targetEntryMethodName,
            paramsList,
            sleepBeforePollingMillis,
            pollingIntervalMillis
        );
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

        var elements = parseAllElements(annotations);
        if (elements == null) return false;

        for (var possiblyClassElement : roundEnv.getElementsAnnotatedWith(elements.computeClassAnnotationElement)) {
            var classInfoStrings = parseClass((TypeElement) possiblyClassElement, elements);
            if (classInfoStrings == null) return false;

            System.err.println(classInfoStrings);

        }
        return true;
    }
}
