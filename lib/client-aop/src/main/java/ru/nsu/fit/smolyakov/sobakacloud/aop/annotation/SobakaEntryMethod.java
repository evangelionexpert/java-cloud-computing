package ru.nsu.fit.smolyakov.sobakacloud.aop.annotation;

import ru.nsu.fit.smolyakov.sobakacloud.aop.HttpCloudComputingClient;
import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.processor.CloudComputeProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface SobakaEntryMethod {
    String targetName() default CloudComputeProcessor.EMPTY_VALUE;
    int pollingIntervalMillis() default HttpCloudComputingClient.DEFAULT_POLLING_INTERVAL_MILLIS;
    int sleepBeforePollingMillis() default HttpCloudComputingClient.DEFAULT_SLEEP_BEFORE_POLLING_MILLIS;
}
