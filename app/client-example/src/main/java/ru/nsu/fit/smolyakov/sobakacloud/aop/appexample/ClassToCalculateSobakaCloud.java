package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

import ru.nsu.fit.smolyakov.sobakacloud.aop.HttpCloudComputingClient;

import java.io.IOException;
import java.util.List;

public class ClassToCalculateSobakaCloud {
    public static double mememe(double a, double b) {
        try {
            return (double) HttpCloudComputingClient.send(
                ClassToCalculate.class,
                "mememe",
                List.of(double.class, double.class),
                List.of(a, b),
                double.class,
                1000,
                1000
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
