package com.example.system.nablarch.handler.csrf;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CsrfTokenVerificationIT extends ExampleChatRestTestBase {

    @Test
    public void CSRFトークンのエラー情報が返却できる() {
        RestMockHttpRequest request = post("/api/login")
                .setContentType("application/json")
                .setBody(Map.of("mailAddress", "dummy", "password", "dummy"));
        HttpResponse response = sendRequest(request);

        assertEquals(400, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "csrf.invalid");
    }
}
