package ru.nsu.fit.smolyakov.sobakacloud.server;

import jakarta.servlet.MultipartConfigElement;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.MultiPartFormDataCompliance;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.nio.file.Path;

public class ComputingServer {
    private final int port;
    private final int computingTimeout;

    public final static int DEFAULT_PORT = 8080;
    public final static int DEFAULT_COMPUTING_TIMEOUT = 0;

    private ComputingServer(int port, int computingTimeout) {
        this.port = port;
        this.computingTimeout = computingTimeout;
    }

    public static class ComputingServerBuilder {
        private final int computingTimeout;
        private final int port;

        private ComputingServerBuilder(int port, int computingTimeout) {
            this.port = port;
            this.computingTimeout = computingTimeout;
        }

        public ComputingServerBuilder port(int port) {
            if (port < 0 || port > 65535) {
                throw new IllegalArgumentException("port no must be between 0 and 65535");
            }

            return new ComputingServerBuilder(port, this.computingTimeout);
        }

        public ComputingServerBuilder computingTimeout(int computingTimeout) {
            return new ComputingServerBuilder(this.port, computingTimeout);
        }

        public ComputingServer build() {
            return new ComputingServer(this.port, this.computingTimeout);
        }
    }

    public static ComputingServerBuilder builder() {
        return new ComputingServerBuilder(DEFAULT_PORT, DEFAULT_COMPUTING_TIMEOUT);
    }

    public void start() throws Exception {
        Server server = new Server(this.port);
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setMultiPartFormDataCompliance(MultiPartFormDataCompliance.RFC7578);

        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(0);
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setBaseResource(new PathResource(Path.of("unused")));

        long maxFileSize = Long.MAX_VALUE;
        long maxRequestSize = Long.MAX_VALUE;
        int fileSizeThreshold = (int)(2 * 1024 * 1024);// todo мб вынести

        MultipartConfigElement multipartConfig = new MultipartConfigElement(null, maxFileSize, maxRequestSize, fileSizeThreshold);
        ServletHolder holder = context.addServlet(ComputingServlet.class, "/multipart");
        holder.getRegistration().setMultipartConfig(multipartConfig);

        server.setHandler(context);
        server.start();
        server.join();
    }
}
