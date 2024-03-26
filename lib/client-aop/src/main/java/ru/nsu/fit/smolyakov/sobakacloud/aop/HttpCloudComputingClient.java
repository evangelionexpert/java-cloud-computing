package ru.nsu.fit.smolyakov.sobakacloud.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesRequestContent;
import org.eclipse.jetty.client.util.MultiPartRequestContent;
import org.eclipse.jetty.http.HttpMethod;
import ru.nsu.fit.smolyakov.sobakacloud.aop.util.Util;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.ArgDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.TaskResultResponseDto;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.TaskSubmitRequestDto;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class HttpCloudComputingClient {
    public static final int DEFAULT_SLEEP_BEFORE_POLLING_MILLIS = 0;
    public static final int DEFAULT_POLLING_INTERVAL_MILLIS = 1000;

    public static Object send(Class<?> clazz,
                              String methodName,
                              List<Class<?>> argTypes,
                              List<Object> args,
                              Class<?> retType,
                              long sleepBeforePollingMillis,
                              long pollingIntervalMillis) {
        if (Objects.requireNonNull(argTypes).size() != Objects.requireNonNull(args).size()) {
            throw new IllegalArgumentException();
        }

        var argDtos = IntStream.range(0, args.size())
            .mapToObj(i ->
                ArgDto.Type.fromClass(argTypes.get(i))
                    .orElseThrow()
                    .createDto(args.get(i))
            ).toList();

        byte[] requestDtoBytes;
        try {
            requestDtoBytes = new TaskSubmitRequestDto(
                methodName,
                argDtos
            ).serialize();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        byte[] clazzBytes;
        try {
            clazzBytes = Util.getClassFileBytes(clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MultiPartRequestContent multiPart = new MultiPartRequestContent();
        multiPart.addFieldPart("classFile", new BytesRequestContent(clazzBytes), null);
        multiPart.addFieldPart("requestInfo", new BytesRequestContent(requestDtoBytes), null);
        multiPart.close();

        HttpClient httpClient = new HttpClient();
        TaskResultResponseDto resultDto;
        try {
            httpClient.start();

            ContentResponse response;

            response = httpClient.newRequest("http://localhost:8080/compute/submit")
                .method(HttpMethod.POST)
                .body(multiPart)
                .send();

            long id = Long.parseLong(response.getContentAsString());

            Thread.sleep(Long.max(0, sleepBeforePollingMillis - pollingIntervalMillis));
            do {
                Thread.sleep(pollingIntervalMillis);
                response = httpClient.newRequest("http://localhost:8080/compute/result?id=" + id)
                    .method(HttpMethod.GET)
                    .send();

                resultDto = TaskResultResponseDto.deserialize(response.getContentAsString());
            } while (resultDto.getStatus().equals(TaskResultResponseDto.Status.IN_PROCESS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return switch (resultDto.getStatus()) {
            case SUCCESS -> ((TaskResultResponseDto.Success) resultDto).getArgDto().getArgValueAsObject();
            case FAILURE -> throw ((TaskResultResponseDto.Failure) resultDto).getSobakaExecutionException();
            case IN_PROCESS -> throw new IllegalStateException("not possible");
        };
    }
}
