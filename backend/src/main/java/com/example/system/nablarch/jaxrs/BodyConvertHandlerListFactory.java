package com.example.system.nablarch.jaxrs;

import nablarch.fw.Handler;
import nablarch.fw.jaxrs.*;
import nablarch.fw.web.HttpRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BodyConvertHandlerListFactory implements JaxRsHandlerListFactory {

    private final List<Handler<HttpRequest, ?>> handlers;

    private List<Handler<HttpRequest, ?>> preHandlers = Collections.emptyList();

    public BodyConvertHandlerListFactory() {
        BodyConvertHandler bodyConvertHandlers = new BodyConvertHandler();
        // Date and Time APIをJacksonで変換するためJackson2BodyConverterを拡張
        bodyConvertHandlers.addBodyConverter(new BodyConverter());
        // "multipart/form-data"に対応
        bodyConvertHandlers.addBodyConverter(new MultipartBodyConverter());

        handlers = List.of(bodyConvertHandlers);
    }

    @Override
    public List<Handler<HttpRequest, ?>> createObject() {
        return Stream.concat(preHandlers.stream(), handlers.stream()).collect(Collectors.toUnmodifiableList());
    }

    public void setPreHandlers(List<Handler<HttpRequest, ?>> preHandlers) {
        this.preHandlers = preHandlers;
    }
}
