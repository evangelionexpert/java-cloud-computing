package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

public class TaskResultResponseDto {
    public TaskResultResponseDto(@JsonProperty("result") ArgDto result) {
        this.result = result;
    }

    public byte[] serialize() {
        try {
            return JacksonMappingSingleton.INSTANCE.getWriter().writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //todo custom
        }
    }

    private final ArgDto result;

    public ArgDto getResult() {
        return result;
    }
}
