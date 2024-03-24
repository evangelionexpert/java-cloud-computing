package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TaskSubmitRequestDto {
    private final String entryMethodName;
    private final List<ArgDto> args;

    public TaskSubmitRequestDto(@JsonProperty("entryMethodName") String entryMethodName,
                                @JsonProperty("args") List<ArgDto> args) {
        this.entryMethodName = entryMethodName;
        this.args = args;
    }

    public static TaskSubmitRequestDto deserialize(InputStream stream) {
        try {
            return JacksonMappingSingleton.INSTANCE.getMapper().readValue(stream, TaskSubmitRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);  // todo custom
        }
    }

    public static TaskSubmitRequestDto deserialize(byte[] bytes) {
        try {
            return JacksonMappingSingleton.INSTANCE.getMapper().readValue(bytes, TaskSubmitRequestDto.class);
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
