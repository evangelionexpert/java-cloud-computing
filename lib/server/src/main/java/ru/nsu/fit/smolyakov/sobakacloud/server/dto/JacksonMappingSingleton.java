package ru.nsu.fit.smolyakov.sobakacloud.server.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public enum JacksonMappingSingleton {
    INSTANCE;

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writer();

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ObjectWriter getWriter() {
        return writer;
    }
}
