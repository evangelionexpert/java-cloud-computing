package ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.processor;

import com.google.auto.service.AutoService;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.ArgDto;

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
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
    public static final String HTTP_CLIENT_QUALIFIED_METHOD_NAME =
        "ru.nsu.fit.smolyakov.sobakacloud.aop.HttpCloudComputingClient.send";

    public String generateClassSourceString(ClassInfo classInfo) {
        StringBuilder typesAndArgs = new StringBuilder();
        StringBuilder types = new StringBuilder();
        StringBuilder argNames = new StringBuilder();

        if (!classInfo.params.isEmpty()) {
            var pair = classInfo.params.get(0);
            typesAndArgs.append(pair.paramType).append(" ").append(pair.paramName);
            types.append(pair.paramType).append(".class");
            argNames.append(pair.paramName);

            for (int i = 1; i < classInfo.params.size(); i++) {
                pair = classInfo.params.get(i);
                typesAndArgs.append(", ").append(pair.paramType).append(" ").append(pair.paramName);
                types.append(", ").append(pair.paramType).append(".class");
                argNames.append(", ").append(pair.paramName);
            }
        }

        String res = "package " + classInfo.targetPackage + ";\n" +
            "public class " + classInfo.targetShortClassName + " {\n" +
            "public static " + classInfo.returnType + " " + classInfo.targetEntryMethodName +
            "(" + typesAndArgs + ") {\n" +
            "return (" + classInfo.returnType + ") " + HTTP_CLIENT_QUALIFIED_METHOD_NAME + "(\n" +
            classInfo.sourcePackage + "." + classInfo.sourceShortClassName + ".class,\n" +
            "\"" + classInfo.sourceEntryMethodName + "\",\n" +
            "java.util.List.of(" + types + "),\n" +
            "java.util.List.of(" + argNames + "),\n" +
            classInfo.returnType + ".class,\n" +
            classInfo.sleepBeforePollingMillis + ", " + classInfo.pollingIntervalMillis + ");\n" +
            "}\n}\n";

        return res;
    }

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

    private Object getElementValueFromAnnotationMirror(AnnotationMirror annotationMirror, ExecutableElement e) {
        AnnotationValue value = annotationMirror.getElementValues().get(e);
        if (value == null) {
            value = e.getDefaultValue();
        }
        return value.getValue();
    }

    private ClassInfo parseClass(TypeElement classElement, AllElements e) {
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

        if (!method.element.getThrownTypes().isEmpty()) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "sorry, checked exceptions are not supported"
            );
            return null;
        }
        if (!method.element.getModifiers().contains(Modifier.PUBLIC)
            || !method.element.getModifiers().contains(Modifier.STATIC)) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "sorry, method must be public static"
            );
            return null;
        }


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

        List<Param> params = method.element.getParameters().stream()
            .map(param ->
                new Param(
                    param.asType().toString(),
                    param.getSimpleName().toString()
                )
            ).toList();

        return new ClassInfo(
            returnType,
            sourcePackage,
            sourceShortClassName,
            targetPackage,
            targetShortClassName,
            sourceEntryMethodName,
            targetEntryMethodName,
            params,
            sleepBeforePollingMillis,
            pollingIntervalMillis
        );
    }

    public ClassInfo setDefaultValues(ClassInfo classInfo) {
        String targetPackage = classInfo.targetPackage;
        if (targetPackage.equals(EMPTY_VALUE)) {
            targetPackage = classInfo.sourcePackage;
        }

        String targetShortClassName = classInfo.targetShortClassName;
        if (targetShortClassName.equals(EMPTY_VALUE)) {
            targetShortClassName = classInfo.sourceShortClassName + DEFAULT_CLASS_SUFFIX;
        }

        String targetEntryMethodName = classInfo.targetEntryMethodName;
        if (targetEntryMethodName.equals(EMPTY_VALUE)) {
            targetEntryMethodName = classInfo.sourceEntryMethodName;
        }

        return new ClassInfo(
            classInfo.returnType,
            classInfo.sourcePackage,
            classInfo.sourceShortClassName,
            targetPackage,
            targetShortClassName,
            classInfo.sourceEntryMethodName,
            targetEntryMethodName,
            classInfo.params,
            classInfo.sleepBeforePollingMillis,
            classInfo.pollingIntervalMillis
        );
    }

    public boolean validate(ClassInfo classInfo) {
        boolean unsupportedTypeInParams = classInfo.params
            .stream()
            .map(Param::paramType)
            .anyMatch(paramName -> ArgDto.Type.fromString(paramName).isEmpty());

        boolean unsupportedReturnType = ArgDto.Type.fromString(classInfo.returnType).isEmpty();

        if (unsupportedReturnType || unsupportedTypeInParams) {
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "unsupported type. check ArgDto.Type, please"
            );
            return false;
        } else {
            return true;
        }
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
            var classInfo = parseClass((TypeElement) possiblyClassElement, elements);
            if (classInfo == null) return false;

            classInfo = setDefaultValues(classInfo);

            System.err.println(classInfo);
            if (!validate(classInfo)) return false;

            System.err.println(generateClassSourceString(classInfo));

            JavaFileObject sourceFile;
            try {
                sourceFile = processingEnv.getFiler().createSourceFile(classInfo.targetPackage + "." + classInfo.targetShortClassName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (OutputStream outputStream = sourceFile.openOutputStream()) {
                outputStream.write(generateClassSourceString(classInfo).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
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
    ) {
    }

    private record AnnotatedMethod(
        AnnotationMirror annotationMirror,
        ExecutableElement element
    ) {
    }

    private record Param(
        String paramType,
        String paramName
    ) {
    }

    private record ClassInfo(
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
    ) {
    }
}
