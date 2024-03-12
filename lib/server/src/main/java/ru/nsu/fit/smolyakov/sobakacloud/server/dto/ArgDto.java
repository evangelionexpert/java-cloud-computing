package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "argType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ArgDto.IntegerArgDto.class, name="int"),
    @JsonSubTypes.Type(value = ArgDto.LongArgDto.class, name="long"),
    @JsonSubTypes.Type(value = ArgDto.DoubleArgDto.class, name="double"),
    @JsonSubTypes.Type(value = ArgDto.FloatArgDto.class, name="float"),
})
public abstract class ArgDto {
    @JsonProperty("argType")
    private String argType;

    public static class IntegerArgDto extends ArgDto {
        @JsonProperty("argValue")
        int argValue;
    }

    public static class LongArgDto extends ArgDto {
        @JsonProperty("argValue")
        long argValue;
    }

    public static class DoubleArgDto extends ArgDto {
        @JsonProperty("argValue")
        double argValue;
    }

    public static class FloatArgDto extends ArgDto {
        @JsonProperty("argValue")
        float argValue;
    }
}
