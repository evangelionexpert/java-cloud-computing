package ru.nsu.fit.smolyakov.sobakacloud.aop;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesRequestContent;
import org.eclipse.jetty.client.util.MultiPartRequestContent;
import org.eclipse.jetty.http.HttpMethod;
import ru.nsu.fit.smolyakov.sobakacloud.aop.util.Util;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.ArgDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.TaskSubmitRequestDto;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class HttpCloudComputingClient {
    public static Object send(Class<?> clazz,
                              String methodName,
                              List<Class<?>> argTypes,
                              List<Object> args,
                              Class<?> retType,
                              long pollingIntervalMillis)
        throws IOException, ClassNotFoundException {

        if (Objects.requireNonNull(argTypes).size() != Objects.requireNonNull(args).size()) {
            throw new IllegalArgumentException();
        }

        var argDtos = IntStream.range(0, args.size())
            .mapToObj(i ->
                ArgDto.Type.fromClass(argTypes.get(i))
                    .orElseThrow()
                    .createDto(args.get(i))
            ).toList();

        var requestDtoBytes = new TaskSubmitRequestDto(
            methodName,
            argDtos
        ).serialize();

        var clazzBytes = Util.getClassFileBytes(clazz);

        MultiPartRequestContent multiPart = new MultiPartRequestContent();
        multiPart.addFieldPart("classFile", new BytesRequestContent(clazzBytes), null);
        multiPart.addFieldPart("requestInfo", new BytesRequestContent(requestDtoBytes), null);
        multiPart.close();

        HttpClient httpClient = new HttpClient();

        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ContentResponse response;

        try {
            response = httpClient.newRequest("http://localhost:8080/compute/submit")
                .method(HttpMethod.POST)
                .body(multiPart)
                .send();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        long id = Long.parseLong(response.getContentAsString());
        try {
            response = httpClient.newRequest("http://localhost:8080/compute/result?id=" + id)
                .method(HttpMethod.GET)
                .send();
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return (double) 5;
    }
}
