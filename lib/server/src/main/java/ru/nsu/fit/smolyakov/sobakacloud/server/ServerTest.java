package ru.nsu.fit.smolyakov.sobakacloud.server;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.io.LogarithmicArrayByteBufferPool;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.MultiPartFormDataCompliance;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;

public class ServerTest {
    public static void soma() throws Exception {
        // Создаем сервер Jetty
        Server server = new Server(8080);
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setMultiPartFormDataCompliance(MultiPartFormDataCompliance.RFC7578);

        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(0);
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
//        context.setContextPath("/");
        context.setBaseResource(new PathResource(Path.of("randomfolre")));

        long maxFileSize = Long.MAX_VALUE;
        long maxRequestSize = Long.MAX_VALUE;
        int fileSizeThreshold = (int)(2 * 1024 * 1024);

        MultipartConfigElement multipartConfig = new MultipartConfigElement(null, maxFileSize, maxRequestSize, fileSizeThreshold);
        ServletHolder holder = context.addServlet(FileUploadServlet.class, "/multipart");
        holder.getRegistration().setMultipartConfig(multipartConfig);

        server.setHandler(context);
        server.start();
        server.join();
    }

    public static class FileUploadServlet extends DefaultServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            req.getParts().forEach(part -> {
                InputStream stream;
                try {
                    stream = part.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                byte[] bytes;
                try {
                    bytes = stream.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("name is " + part.getName());
                System.out.println(Arrays.toString(bytes));
            });
        }
    }
}