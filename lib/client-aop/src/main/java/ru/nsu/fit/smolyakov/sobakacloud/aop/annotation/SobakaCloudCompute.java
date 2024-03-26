package ru.nsu.fit.smolyakov.sobakacloud.aop.annotation;

import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.processor.CloudComputeProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface SobakaCloudCompute {
    String server();

    String targetShortClassName() default CloudComputeProcessor.EMPTY_VALUE;

    String targetPackage() default CloudComputeProcessor.EMPTY_VALUE;
}
