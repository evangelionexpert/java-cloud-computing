package ru.nsu.fit.smolyakov.sobakacloud.aop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "argType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ArgDto.ByteArgDto.class, name = "byte"),
    @JsonSubTypes.Type(value = ArgDto.ShortArgDto.class, name = "short"),
    @JsonSubTypes.Type(value = ArgDto.IntArgDto.class, name = "int"),
    @JsonSubTypes.Type(value = ArgDto.LongArgDto.class, name = "long"),
    @JsonSubTypes.Type(value = ArgDto.FloatArgDto.class, name = "float"),
    @JsonSubTypes.Type(value = ArgDto.DoubleArgDto.class, name = "double"),
    @JsonSubTypes.Type(value = ArgDto.BooleanArgDto.class, name = "boolean"),
    @JsonSubTypes.Type(value = ArgDto.CharArgDto.class, name = "char"),

    @JsonSubTypes.Type(value = ArgDto.ByteArrayArgDto.class, name = "byte[]"),
    @JsonSubTypes.Type(value = ArgDto.ShortArrayArgDto.class, name = "short[]"),
    @JsonSubTypes.Type(value = ArgDto.IntArrayArgDto.class, name = "int[]"),
    @JsonSubTypes.Type(value = ArgDto.LongArrayArgDto.class, name = "long[]"),
    @JsonSubTypes.Type(value = ArgDto.FloatArrayArgDto.class, name = "float[]"),
    @JsonSubTypes.Type(value = ArgDto.DoubleArrayArgDto.class, name = "double[]"),
    @JsonSubTypes.Type(value = ArgDto.BooleanArrayArgDto.class, name = "boolean[]"),
    @JsonSubTypes.Type(value = ArgDto.CharArrayArgDto.class, name = "char[]"),

    @JsonSubTypes.Type(value = ArgDto.StringArgDto.class, name = "java.lang.String"),
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
        @JsonProperty("byte") BYTE(byte.class, "byte", (Object value) -> new ByteArgDto((byte) value)),
        @JsonProperty("short") SHORT(short.class, "short", (Object value) -> new ShortArgDto((short) value)),
        @JsonProperty("int") INT(int.class, "int", (Object value) -> new IntArgDto((int) value)),
        @JsonProperty("long") LONG(long.class, "long", (Object value) -> new LongArgDto((long) value)),
        @JsonProperty("float") FLOAT(float.class, "float", (Object value) -> new FloatArgDto((float) value)),
        @JsonProperty("double") DOUBLE(double.class, "double", (Object value) -> new DoubleArgDto((double) value)),
        @JsonProperty("boolean") BOOLEAN(boolean.class, "boolean", (Object value) -> new BooleanArgDto((boolean) value)),
        @JsonProperty("char") CHAR(char.class, "char", (Object value) -> new CharArgDto((char) value)),

        @JsonProperty("byte[]") BYTE_ARRAY(byte[].class, "byte[]", (Object value) -> new ByteArrayArgDto((byte[]) value)),
        @JsonProperty("short[]") SHORT_ARRAY(short[].class, "short[]", (Object value) -> new ShortArrayArgDto((short[]) value)),
        @JsonProperty("int[]") INT_ARRAY(int[].class, "int[]", (Object value) -> new IntArrayArgDto((int[]) value)),
        @JsonProperty("long[]") LONG_ARRAY(long[].class, "long[]", (Object value) -> new LongArrayArgDto((long[]) value)),
        @JsonProperty("float[]") FLOAT_ARRAY(float[].class, "float[]", (Object value) -> new FloatArrayArgDto((float[]) value)),
        @JsonProperty("double[]") DOUBLE_ARRAY(double[].class, "double[]", (Object value) -> new DoubleArrayArgDto((double[]) value)),
        @JsonProperty("boolean[]") BOOLEAN_ARRAY(boolean[].class, "boolean[]", (Object value) -> new BooleanArrayArgDto((boolean[]) value)),
        @JsonProperty("char[]") CHAR_ARRAY(char[].class, "char[]", (Object value) -> new CharArrayArgDto((char[]) value)),

        @JsonProperty("java.lang.String") STRING(String.class, "java.lang.String", (Object value) -> new StringArgDto((String) value));

        private final Function<Object, ArgDto> constructor;
        private final Class<?> clazz;
        private final String name;

        Type(Class<?> clazz, String name, Function<Object, ArgDto> constructor) {
            this.clazz = clazz;
            this.name = name;
            this.constructor = constructor;
        }

        public static Optional<Type> fromString(String s) {
            return Arrays.stream(Type.values())
                .filter(type -> type.name.equals(s))
                .findAny();
        }

        public static Optional<Type> fromClass(Class<?> clazz) {
            return Arrays.stream(Type.values())
                .filter(type -> type.clazz.equals(clazz))
                .findAny();
        }

        public Class<?> getAssociatedClass() {
            return clazz;
        }

        public ArgDto createDto(Object value) {
            return constructor.apply(value);
        }

        public String getName() {
            return name;
        }
    }

    //primitives
    public static class ByteArgDto extends ArgDto {
        public ByteArgDto(@JsonProperty("argValue") byte argValue) {
            super(Type.BYTE);
            setArgValue(argValue);
        }
    }


    public static class ShortArgDto extends ArgDto {
        public ShortArgDto(@JsonProperty("argValue") short argValue) {
            super(Type.SHORT);
            setArgValue(argValue);
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

    public static class FloatArgDto extends ArgDto {
        public FloatArgDto(@JsonProperty("argValue") float argValue) {
            super(Type.FLOAT);
            setArgValue(argValue);
        }
    }

    public static class DoubleArgDto extends ArgDto {
        public DoubleArgDto(@JsonProperty("argValue") double argValue) {
            super(Type.DOUBLE);
            setArgValue(argValue);
        }
    }

    public static class CharArgDto extends ArgDto {
        public CharArgDto(@JsonProperty("argValue") char argValue) {
            super(Type.CHAR);
            setArgValue(argValue);
        }
    }

    public static class BooleanArgDto extends ArgDto {
        public BooleanArgDto(@JsonProperty("argValue") boolean argValue) {
            super(Type.BOOLEAN);
            setArgValue(argValue);
        }
    }

    // str
    public static class StringArgDto extends ArgDto {
        public StringArgDto(@JsonProperty("argValue") String argValue) {
            super(Type.STRING);
            setArgValue(argValue);
        }
    }


    //arrays

    public static class IntArrayArgDto extends ArgDto {
        public IntArrayArgDto(@JsonProperty("argValue") int[] argValue) {
            super(Type.INT_ARRAY);
            setArgValue(argValue);
        }
    }

    public static class ByteArrayArgDto extends ArgDto {
        public ByteArrayArgDto(@JsonProperty("argValue") byte[] argValue) {
            super(Type.BYTE_ARRAY);
            setArgValue(argValue);
        }
    }

    public static class ShortArrayArgDto extends ArgDto {
        public ShortArrayArgDto(@JsonProperty("argValue") short[] argValue) {
            super(Type.SHORT_ARRAY);
            setArgValue(argValue);
        }
    }

    public static class LongArrayArgDto extends ArgDto {
        public LongArrayArgDto(@JsonProperty("argValue") long[] argValue) {
            super(Type.LONG_ARRAY);
            setArgValue(argValue);
        }
    }

    public static class CharArrayArgDto extends ArgDto {
        public CharArrayArgDto(@JsonProperty("argValue") char[] argValue) {
            super(Type.CHAR_ARRAY);
            setArgValue(argValue);
        }
    }

    public static class BooleanArrayArgDto extends ArgDto {
        public BooleanArrayArgDto(@JsonProperty("argValue") boolean[] argValue) {
            super(Type.BOOLEAN_ARRAY);
            setArgValue(argValue);
        }
    }

    public static class FloatArrayArgDto extends ArgDto {
        public FloatArrayArgDto(@JsonProperty("argValue") float[] argValue) {
            super(Type.FLOAT_ARRAY);
            setArgValue(argValue);
        }
    }

    public static class DoubleArrayArgDto extends ArgDto {
        public DoubleArrayArgDto(@JsonProperty("argValue") double[] argValue) {
            super(Type.DOUBLE_ARRAY);
            setArgValue(argValue);
        }
    }

}
