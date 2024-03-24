package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "argType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ArgDto.IntArgDto.class, name = "int"),
    @JsonSubTypes.Type(value = ArgDto.LongArgDto.class, name = "long"),
    @JsonSubTypes.Type(value = ArgDto.DoubleArgDto.class, name = "double"),
    @JsonSubTypes.Type(value = ArgDto.FloatArgDto.class, name = "float"),
    @JsonSubTypes.Type(value = ArgDto.ExceptionStringArgDto.class, name = "exceptionString"),
})
public abstract class ArgDto {
    private final String argType;

    protected ArgDto(@JsonProperty("argType") String argType) {
        this.argType = Objects.requireNonNull(argType);
    }

    public static ArgDto argDtoFromValue(Object value, Class<?> clazz) {
        if (clazz.equals(int.class)) {
            return new IntArgDto("int", (int) value);
        } else if (clazz.equals(long.class)) {
            return new LongArgDto("long", (long) value);
        } else if (clazz.equals(double.class)) {
            return new DoubleArgDto("double", (double) value);
        } else if (clazz.equals(float.class)) {
            return new FloatArgDto("float", (float) value);
        } else {
            throw new RuntimeException("no such option"); // todo custom
        }
    }

    public abstract Object getArgValueAsObject();

    public abstract Class<?> getArgValueClass();

//    public static abstract class ArrDto extends ArgDto {
//        public abstract Class<?> getInnerClass();
//    }

    public static class IntArgDto extends ArgDto {
        private final int argValue;

        public IntArgDto(@JsonProperty("argType") String argType,
                             @JsonProperty("argValue") int argValue) {
            super(argType);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return int.class;
        }
    }

    public static class LongArgDto extends ArgDto {
        private final long argValue;

        public LongArgDto(@JsonProperty("argType") String argType,
                          @JsonProperty("argValue") long argValue) {
            super(argType);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return long.class;
        }
    }

    public static class DoubleArgDto extends ArgDto {
        private final double argValue;

        public DoubleArgDto(@JsonProperty("argType") String argType,
                            @JsonProperty("argValue") double argValue) {
            super(argType);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return double.class;
        }
    }

    public static class FloatArgDto extends ArgDto {
        private final float argValue;

        public FloatArgDto(@JsonProperty("argType") String argType,
                           @JsonProperty("argValue") float argValue) {
            super(argType);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return float.class;
        }
    }

    public static class ExceptionStringArgDto extends ArgDto {
        private final String argValue;

        public ExceptionStringArgDto(@JsonProperty("argType") String argType,
                           @JsonProperty("argValue") String argValue) {
            super(argType);
            this.argValue = argValue;
        }

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return String.class;
        }
    }
}
