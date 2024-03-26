package ru.nsu.fit.smolyakov.sobakacloud.server.app;


import ru.nsu.fit.smolyakov.sobakacloud.server.ComputingServer;

import java.io.IOException;
import java.net.URISyntaxException;

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
