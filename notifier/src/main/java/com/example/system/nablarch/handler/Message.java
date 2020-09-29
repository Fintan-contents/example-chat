package com.example.system.nablarch.handler;

import java.util.Map;

import com.example.domain.model.Destinations;
import com.example.domain.model.Payload;

import nablarch.fw.Request;

public class Message implements Request<Void> {

    private String requestPath;
    private Destinations destinations;
    private Payload payload;

    public Destinations getDestinations() {
        return destinations;
    }

    public void setDestinations(Destinations destinations) {
        this.destinations = destinations;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public Request<Void> setRequestPath(String requestPath) {
        this.requestPath = requestPath;
        return this;
    }

    @Override
    public Void getParam(String name) {
        return null;
    }

    @Override
    public Map<String, Void> getParamMap() {
        return Map.of();
    }
}
