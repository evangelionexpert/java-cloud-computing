package ru.nsu.fit.smolyakov.sobakacloud.server.app;


import ru.nsu.fit.smolyakov.sobakacloud.server.ComputingServer;

public class Main {
    public static void main(String[] args) {
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
