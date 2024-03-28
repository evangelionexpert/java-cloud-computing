package ru.nsu.fit.smolyakov.sobakacloud.aop.dto;

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

    public static TaskSubmitRequestDto deserialize(InputStream stream) throws IOException {
        return JacksonMappingSingleton.INSTANCE.getMapper().readValue(stream, TaskSubmitRequestDto.class);
    }

    public static TaskSubmitRequestDto deserialize(byte[] bytes) throws IOException {
        return JacksonMappingSingleton.INSTANCE.getMapper().readValue(bytes, TaskSubmitRequestDto.class);
    }

    public byte[] serialize() throws JsonProcessingException {
        return JacksonMappingSingleton.INSTANCE.getWriter().writeValueAsBytes(this);
    }

    public String getEntryMethodName() {
        return entryMethodName;
    }

    public List<ArgDto> getArgs() {
        return args;
    }
}
