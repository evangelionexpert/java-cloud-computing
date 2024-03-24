package ru.nsu.fit.smolyakov.sobakacloud.server;

import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.handler.ErrorHandler;

import java.io.IOException;
import java.io.Writer;

public class JsonErrorHandler extends ErrorHandler {
    @Override
    protected void writeErrorPage(HttpServletRequest req, Writer writer, int code, String msg, boolean showStacks) throws IOException {
        writer.write("{\"msg\":\"" + msg + "\"}");
    }
}
