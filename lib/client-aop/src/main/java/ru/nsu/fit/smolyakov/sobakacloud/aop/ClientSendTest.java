package ru.nsu.fit.smolyakov.sobakacloud.aop;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesRequestContent;
import org.eclipse.jetty.client.util.MultiPartRequestContent;
import org.eclipse.jetty.client.util.PathRequestContent;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class ClientSendTest {
    public void a() throws IOException {
        MultiPartRequestContent multiPart = new MultiPartRequestContent();
        multiPart.addFieldPart("bytes-buf", new BytesRequestContent(new byte[]{'a', 'b'}), null);
        multiPart.addFieldPart("some-file", new PathRequestContent(Path.of("/home/pivo/Desktop/ld audit studying.txt")), null);
        multiPart.close();

        HttpClient httpClient = new HttpClient();

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            ContentResponse response = httpClient.newRequest("http://localhost:8080")
                .method(HttpMethod.POST)
                .body(multiPart)
                .send();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
