package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseDto {
    @JsonProperty("result")
    private ArgDto result;

    public ArgDto getResult() {
        return result;
    } // todo exceptions
}
