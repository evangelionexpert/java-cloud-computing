package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaCloudCompute;
import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaEntryMethod;

@SobakaCloudCompute(server = "localhost:8080")
public class ClassToCalculate {
    @SobakaEntryMethod
    public static double mememe(double a, double b) {
//        throw new IllegalArgumentException("eblo");
        return a+b;
    }
}
