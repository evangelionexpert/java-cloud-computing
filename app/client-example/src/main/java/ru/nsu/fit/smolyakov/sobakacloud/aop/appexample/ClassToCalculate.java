package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaCloudCompute;
import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaEntryMethod;

@SobakaCloudCompute(server = "localhost:8080")
public class ClassToCalculate {
    @SobakaEntryMethod
    public static int calculateMe(String some) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        throw new NullPointerException("this is exception");
    }
}
