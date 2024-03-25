package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import ru.nsu.fit.smolyakov.sobakacloud.server.exceptions.SobakaExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "status", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TaskResultResponseDto.Success.class, name = "success"),
    @JsonSubTypes.Type(value = TaskResultResponseDto.Failure.class, name = "failure"),
    @JsonSubTypes.Type(value = TaskResultResponseDto.InProgress.class, name = "inProgress")
})
public abstract sealed class TaskResultResponseDto
    permits TaskResultResponseDto.Failure, TaskResultResponseDto.InProgress, TaskResultResponseDto.Success {

    private TaskResultResponseDto(@JsonProperty("status") Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        @JsonProperty("success") SUCCESS,
        @JsonProperty("failure") FAILURE,
        @JsonProperty("inProgress") IN_PROCESS
    }

    private final Status status;

    public byte[] serialize() throws JsonProcessingException {

        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
            .filterOutAllExcept("exceptionMsg");
        FilterProvider filters = new SimpleFilterProvider()
            .addFilter("exceptionFilter", theFilter);

        return JacksonMappingSingleton.INSTANCE.getWriter().with(filters).writeValueAsBytes(this);
    }

    public static TaskResultResponseDto deserialize(InputStream stream) throws IOException {
        return JacksonMappingSingleton.INSTANCE.getMapper().readValue(stream, TaskResultResponseDto.class);
    }

    public static TaskResultResponseDto deserialize(String s) throws IOException {
        return JacksonMappingSingleton.INSTANCE.getMapper().readValue(s, TaskResultResponseDto.class);
    }

    public static final class Success extends TaskResultResponseDto {
        private final ArgDto argDto;

        public Success(@JsonProperty("argDto") ArgDto argDto) {
            super(Status.SUCCESS);
            this.argDto = Objects.requireNonNull(argDto);
        }

        public ArgDto getArgDto() {
            return argDto;
        }
    }

    public static final class Failure extends TaskResultResponseDto {
        private final SobakaExecutionException sobakaExecutionException;

        public Failure(@JsonProperty("exception") SobakaExecutionException sobakaExecutionException) {
            super(Status.FAILURE);
            this.sobakaExecutionException = sobakaExecutionException;
        }

        public SobakaExecutionException getSobakaExecutionException() {
            return sobakaExecutionException;
        }
    }

    public static final class InProgress extends TaskResultResponseDto {
        public InProgress() {
            super(Status.IN_PROCESS);
        }
    }
}
