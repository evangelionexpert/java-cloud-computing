package ru.nsu.fit.smolyakov.sobakacloud.server.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.JacksonMappingSingleton;
import ru.nsu.fit.smolyakov.sobakacloud.server.dto.TaskResultResponseDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@JsonFilter("exceptionFilter")
public class SobakaExecutionException extends RuntimeException {
    @JsonProperty("exceptionMsg")
    private final String exceptionMsg;

    @JsonCreator
    public SobakaExecutionException(@JsonProperty("exceptionMsg") String exceptionMsg) {
        super(Objects.requireNonNull(exceptionMsg));
        this.exceptionMsg = exceptionMsg;
    }
}
