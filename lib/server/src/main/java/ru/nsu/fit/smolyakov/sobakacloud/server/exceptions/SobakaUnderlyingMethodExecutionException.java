package ru.nsu.fit.smolyakov.sobakacloud.server.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonFilter("exceptionFilter")
public class SobakaUnderlyingMethodExecutionException extends RuntimeException {
    @JsonProperty("exceptionMsg")
    private final String exceptionMsg;

    @JsonCreator
    public SobakaUnderlyingMethodExecutionException(@JsonProperty("exceptionMsg") String exceptionMsg) {
        super(Objects.requireNonNull(exceptionMsg));
        this.exceptionMsg = exceptionMsg;
    }
}
