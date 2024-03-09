package ru.nsu.fit.smolyakov.sobakacloud.aop.appexample;

import ru.nsu.fit.smolyakov.sobakacloud.aop.ClientSendTest;

public class Main {
    public static void main(String[] args) {
        var cla = new ClientSendTest();
        try {
            cla.a();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
