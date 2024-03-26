package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

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
    }
}
