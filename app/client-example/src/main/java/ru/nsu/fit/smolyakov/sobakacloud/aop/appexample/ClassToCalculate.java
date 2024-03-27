package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaCloudCompute;
import ru.nsu.fit.smolyakov.sobakacloud.aop.annotation.SobakaEntryMethod;

@SobakaCloudCompute(server = "localhost:8080")
public class ClassToCalculate {
    @SobakaEntryMethod
    public static int[] mememe(int[] arr, String eee) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] += 5;
        }

        System.err.println(eee);

        return arr;
    }
}
