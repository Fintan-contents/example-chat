package com.example.system.nablarch.handler.csrf;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.common.web.csrf.CsrfTokenUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpRequestHandler;
import nablarch.fw.web.HttpResponse;

public class CsrfTokenEndpointHandler implements HttpRequestHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String valueKey = "csrfTokenValue";
    private String headerNameKey = "csrfTokenHeaderName";
    private String parameterNameKey = "csrfTokenParameterName";

    @Override
    public HttpResponse handle(HttpRequest request, ExecutionContext context) {
        HttpResponse response = new HttpResponse(HttpResponse.Status.OK.getStatusCode());
        response.setContentType("application/json");
        Map<String, String> value = new HashMap<>();
        value.put(valueKey, CsrfTokenUtil.getCsrfToken(context));
        value.put(headerNameKey, CsrfTokenUtil.getHeaderName());
        value.put(parameterNameKey, CsrfTokenUtil.getParameterName());
        byte[] json;
        try {
            json = objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.write(json);
        return response;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }

    public void setHeaderNameKey(String headerNameKey) {
        this.headerNameKey = headerNameKey;
    }

    public void setParameterNameKey(String parameterNameKey) {
        this.parameterNameKey = parameterNameKey;
    }
}
