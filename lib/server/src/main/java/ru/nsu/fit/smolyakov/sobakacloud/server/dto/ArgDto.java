package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

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
    public enum Type {
        @JsonProperty("int") INT(int.class, (Object value) -> new IntArgDto((int) value)),
        @JsonProperty("long") LONG(long.class, (Object value) -> new LongArgDto((long) value)),
        @JsonProperty("double") DOUBLE(double.class, (Object value) -> new DoubleArgDto((double) value)),
        @JsonProperty("float") FLOAT(float.class, (Object value) -> new FloatArgDto((float) value));

        private final Function<Object, ArgDto> function;
        private Class<?> clazz;
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

        Type(Class<?> clazz, Function<Object, ArgDto> function) {
            this.clazz = clazz;
            this.function = function;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public ArgDto createDto(Object value) {
            return function.apply(value);
        }

        public static Optional<Type> fromString(String s) {
            return Optional.ofNullable(Type.namesMap.get(s));
        }

        public static Optional<Type> fromClass(Class<?> clazz) {
            return Optional.ofNullable(Type.classesMap.get(clazz));
        }
    }

    private ArgDto(@JsonProperty("argType") Type argType) {
        this.argType = argType;
    }

    private final Type argType;

    public Type getArgType() {
        return argType;
    }

    public abstract Object getArgValueAsObject();

    public static class IntArgDto extends ArgDto {
        private final int argValue;

        public IntArgDto(@JsonProperty("argValue") int argValue) {
            super(Type.INT);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }
    }

    public static class LongArgDto extends ArgDto {
        private final long argValue;

        public LongArgDto(@JsonProperty("argValue") long argValue) {
            super(Type.LONG);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }
    }

    public static class DoubleArgDto extends ArgDto {
        private final double argValue;

        public DoubleArgDto(@JsonProperty("argValue") double argValue) {
            super(Type.DOUBLE);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }
    }

    public static class FloatArgDto extends ArgDto {
        private final float argValue;

        public FloatArgDto(@JsonProperty("argValue") float argValue) {
            super(Type.FLOAT);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }
    }
}
