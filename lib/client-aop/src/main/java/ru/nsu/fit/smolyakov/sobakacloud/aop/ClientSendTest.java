package ru.nsu.fit.smolyakov.sobakacloud.aop;

import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.MultiPartRequestContent;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MultiPart;
import org.eclipse.jetty.http.MultiPartCompliance;
import org.eclipse.jetty.io.ByteArrayEndPoint;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class ClientSendTest {
    public void a() {
        MultiPartRequestContent multiPart = new MultiPartRequestContent();
        multiPart.addPart(
            new MultiPart.ByteBufferPart(
                "bytes-buf",
                "noname1",
                null,
                ByteBuffer.wrap(new byte[]{'a', 'b'} )
            )
        );

        multiPart.addPart(
            new MultiPart.PathPart(
                "some-file",
                "noname2",
                null,
                Path.of("/home/pivo/Desktop/somefile.txt")
            )
        );

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
