package ru.nsu.fit.smolyakov.sobakacloud.server.app;


import ru.nsu.fit.smolyakov.sobakacloud.server.ServerTest;

public class Main {
    public static void main(String[] args) {
        try {
            ServerTest.soma();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
