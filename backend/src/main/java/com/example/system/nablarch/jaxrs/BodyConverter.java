package com.example.system.nablarch.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nablarch.integration.jaxrs.jackson.Jackson2BodyConverter;

public class BodyConverter extends Jackson2BodyConverter {

    @Override
    protected void configure(ObjectMapper objectMapper) {
        super.configure(objectMapper);

        // Date and Time APIをJacksonで変換するための設定
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }
}
