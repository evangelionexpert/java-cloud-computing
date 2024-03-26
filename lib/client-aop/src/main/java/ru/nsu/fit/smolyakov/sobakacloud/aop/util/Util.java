package ru.nsu.fit.smolyakov.sobakacloud.aop.util;

import java.io.IOException;
import java.util.Objects;

public class Util {
    public static String getClassFilePath(Class<?> clazz) {
        return clazz.getName().replace('.', '/')
            + ".class";
    }

    public static byte[] getClassFileBytes(Class<?> clazz) throws IOException {
        try (var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(getClassFilePath(clazz))) {
            return Objects.requireNonNull(stream).readAllBytes();
        }
    }
}
