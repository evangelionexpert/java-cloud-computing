package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

import ru.nsu.fit.smolyakov.sobakacloud.aop.HttpCloudComputingClient;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        try {
            double c = ClassToCalculateSobakaCloud.mememe(4, 6);
            System.err.println(c);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        var a = Thread.currentThread().getContextClassLoader().getResource("ru/nsu/fit/smolyakov/sobakacloud/aop/appexample/Main.class");
//        System.err.println(a);
    }
}
