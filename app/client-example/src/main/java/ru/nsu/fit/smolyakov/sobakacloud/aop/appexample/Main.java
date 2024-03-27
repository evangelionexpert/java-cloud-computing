package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        try {
            var arr = ClassToCalculateSobakaCloud.mememe(new int[]{1, 2, 3, 4, 5}, "asaba");
            System.err.println(Arrays.toString(arr));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
