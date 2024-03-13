package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RequestDto {
    @JsonProperty("entryMethodName")
    private String entryMethodName;
    @JsonProperty("args")
    private List<ArgDto> args;

    public static RequestDto deserialize(InputStream stream) {
        try {
            return JacksonMappingSingleton.INSTANCE.getMapper().readValue(stream, RequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);  // todo custom
        }
    }

    public static RequestDto deserialize(byte[] bytes) {
        try {
            return JacksonMappingSingleton.INSTANCE.getMapper().readValue(bytes, RequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);  // todo custom
        }
    }

    public byte[] serialize() {
        try {
            return JacksonMappingSingleton.INSTANCE.getWriter().writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //todo custom
        }
    }

    public String getEntryMethodName() {
        return entryMethodName;
    }

    public List<ArgDto> getArgs() {
        return args;
    }
}
