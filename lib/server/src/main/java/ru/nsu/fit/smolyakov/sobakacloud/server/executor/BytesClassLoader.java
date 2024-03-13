package ru.nsu.fit.smolyakov.sobakacloud.server.executor;

import java.security.CodeSource;
import java.security.SecureClassLoader;

public class BytesClassLoader extends SecureClassLoader {
    public BytesClassLoader() {
    }

    public Class<?> loadClassFromBytes(byte[] bytes) {
        return defineClass(null, bytes, 0, bytes.length, (CodeSource) null);
    }
}
