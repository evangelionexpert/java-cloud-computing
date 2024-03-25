package ru.nsu.fit.smolyakov.sobakacloud.server.app;


import ru.nsu.fit.smolyakov.sobakacloud.server.ComputingServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        var server = ComputingServer.builder()
            .port(8080)
            .computingTimeout(100)
            .build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
