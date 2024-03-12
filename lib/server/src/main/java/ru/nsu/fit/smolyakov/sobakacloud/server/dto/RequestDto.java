package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RequestDto {
    @JsonProperty("entryMethodName")
    private String entryMethodName;
    @JsonProperty("args")
    private List<ArgDto> args;

    public String getEntryMethodName() {
        return entryMethodName;
    }

    public List<ArgDto> getArgs() {
        return args;
    }
}
