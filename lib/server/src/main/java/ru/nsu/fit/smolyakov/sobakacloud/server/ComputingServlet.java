package ru.nsu.fit.smolyakov.sobakacloud.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.DefaultServlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ComputingServlet extends DefaultServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        for (var part : req.getParts()) {
            InputStream stream = part.getInputStream();
            byte[] bytes = stream.readAllBytes();

//            System.out.println("name is " + part.getName());
//            System.out.println(Arrays.toString(bytes));
            switch (part.getName()) {
                case "classFile" -> {
                    System.err.println("classFile");
                }
                case "requestInfo" -> {
                    System.err.println("requestInfo");
                }
                default -> {
                    // error
                    System.err.println("error");
                }
            }
        }
    }
}
