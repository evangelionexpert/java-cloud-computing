package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.InputStream;

public class ResponseDto {
    public ResponseDto(@JsonProperty("result") ArgDto result) {
        this.result = result;
    }

    public byte[] serialize() {
        try {
            return JacksonMappingSingleton.INSTANCE.getWriter().writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //todo custom
        }
    }

    private ArgDto result;

    public ArgDto getResult() {
        return result;
    } // todo exceptions
}
