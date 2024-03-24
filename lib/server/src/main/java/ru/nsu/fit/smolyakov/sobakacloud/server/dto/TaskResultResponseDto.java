package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "status", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TaskResultResponseDto.class, name = "success"),
    @JsonSubTypes.Type(value = ArgDto.LongArgDto.class, name = "failure"),
    @JsonSubTypes.Type(value = ArgDto.FloatArgDto.class, name = "inProgress")
})
public abstract class TaskResultResponseDto {
    public TaskResultResponseDto(@JsonProperty("status") Status status) {
        this.status = status;
    }

    public enum Status {
        @JsonProperty("success") SUCCESS,
        @JsonProperty("failure") FAILURE,
        @JsonProperty("inProgress") IN_PROCESS
    }

    private final Status status;

    public byte[] serialize() throws JsonProcessingException {
        return JacksonMappingSingleton.INSTANCE.getWriter().writeValueAsBytes(this);
    }
}
