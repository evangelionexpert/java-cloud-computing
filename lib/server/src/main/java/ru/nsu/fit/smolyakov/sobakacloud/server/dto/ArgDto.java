package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "argType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ArgDto.IntArgDto.class, name = "int"),
    @JsonSubTypes.Type(value = ArgDto.LongArgDto.class, name = "long"),
    @JsonSubTypes.Type(value = ArgDto.DoubleArgDto.class, name = "double"),
    @JsonSubTypes.Type(value = ArgDto.FloatArgDto.class, name = "float"),
})
public abstract class ArgDto {
    @JsonProperty("argType")
    private final Type argType;
    @JsonProperty("argValue")
    private Object argValue;

    private ArgDto(@JsonProperty("argType") Type argType) {
        this.argType = argType;
    }

    protected void setArgValue(Object o) {
        this.argValue = o;
    }

    public Type getArgType() {
        return argType;
    }

    @JsonIgnore
    public Object getArgValueAsObject() {
        return argValue;
    }

    public enum Type {
        @JsonProperty("int") INT(int.class, (Object value) -> new IntArgDto((int) value)),
        @JsonProperty("long") LONG(long.class, (Object value) -> new LongArgDto((long) value)),
        @JsonProperty("double") DOUBLE(double.class, (Object value) -> new DoubleArgDto((double) value)),
        @JsonProperty("float") FLOAT(float.class, (Object value) -> new FloatArgDto((float) value));

        private final static Map<String, Type> namesMap =
            Map.of(
                "int", INT,
                "long", LONG,
                "double", DOUBLE,
                "float", FLOAT
            );
        private final static Map<Class<?>, Type> classesMap =
            Map.of(
                int.class, INT,
                long.class, LONG,
                double.class, DOUBLE,
                float.class, FLOAT
            );
        private final Function<Object, ArgDto> function;
        private final Class<?> clazz;

        Type(Class<?> clazz, Function<Object, ArgDto> function) {
            this.clazz = clazz;
            this.function = function;
        }

        public static Optional<Type> fromString(String s) {
            return Optional.ofNullable(Type.namesMap.get(s));
        }

        public static Optional<Type> fromClass(Class<?> clazz) {
            return Optional.ofNullable(Type.classesMap.get(clazz));
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public ArgDto createDto(Object value) {
            return function.apply(value);
        }
    }

    public static class IntArgDto extends ArgDto {
        public IntArgDto(@JsonProperty("argValue") int argValue) {
            super(Type.INT);
            setArgValue(argValue);
        }
    }

    public static class LongArgDto extends ArgDto {
        public LongArgDto(@JsonProperty("argValue") long argValue) {
            super(Type.LONG);
            setArgValue(argValue);
        }
    }

    public static class DoubleArgDto extends ArgDto {
        public DoubleArgDto(@JsonProperty("argValue") double argValue) {
            super(Type.DOUBLE);
            setArgValue(argValue);
        }
    }

    public static class FloatArgDto extends ArgDto {
        public FloatArgDto(@JsonProperty("argValue") float argValue) {
            super(Type.FLOAT);
            setArgValue(argValue);
        }
    }
}
