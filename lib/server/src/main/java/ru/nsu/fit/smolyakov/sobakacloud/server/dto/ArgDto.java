package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "argType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ArgDto.IntegerArgDto.class, name="int"),
    @JsonSubTypes.Type(value = ArgDto.LongArgDto.class, name="long"),
    @JsonSubTypes.Type(value = ArgDto.DoubleArgDto.class, name="double"),
    @JsonSubTypes.Type(value = ArgDto.FloatArgDto.class, name="float"),
})
public abstract class ArgDto {
    private final String argType;

    protected ArgDto(@JsonProperty("argType") String argType) {
        this.argType = Objects.requireNonNull(argType);
    }

    public abstract Object getArgValueAsObject();

    public abstract Class<?> getArgValueClass();

    public static ArgDto argDtoFromValue(Object value, Class<?> clazz) {
        if (clazz.equals(int.class)) {
            return new IntegerArgDto("int", (int) value);
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

//    public static abstract class GenericsDto extends ArgDto {
//        public abstract Class<?> getInnerClass();
//    } // todo save generic class type. надо или нет???

    public static class IntegerArgDto extends ArgDto {
        int argValue;

        public IntegerArgDto(@JsonProperty("argType") String argType,
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
        long argValue;

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return long.class;
        }

        public LongArgDto(@JsonProperty("argType") String argType,
                          @JsonProperty("argValue") long argValue) {
            super(argType);
            this.argValue = argValue;
        }
    }

    public static class DoubleArgDto extends ArgDto {
        double argValue;

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return double.class;
        }

        public DoubleArgDto(@JsonProperty("argType") String argType,
                            @JsonProperty("argValue") double argValue) {
            super(argType);
            this.argValue = argValue;
        }
    }

    public static class FloatArgDto extends ArgDto {
        float argValue;

        @Override
        public Object getArgValueAsObject() {
            return argValue;
        }

        @Override
        public Class<?> getArgValueClass() {
            return float.class;
        }

        public FloatArgDto(@JsonProperty("argType") String argType,
                           @JsonProperty("argValue") float argValue) {
            super(argType);
            this.argValue = argValue;
        }
    }
}
