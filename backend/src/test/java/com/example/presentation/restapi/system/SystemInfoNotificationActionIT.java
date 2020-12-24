package com.example.presentation.restapi.system;

import static org.junit.Assert.*;

import com.jayway.jsonassert.JsonAssert;
import org.junit.Test;

import com.example.presentation.restapi.ExampleChatRestTestBase;

import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;

public class SystemInfoNotificationActionIT extends ExampleChatRestTestBase {

    @Test
    public void WebSocketの接続URLを取得できる() throws Exception {
        login("user1@example.com", "pass123-");

        RestMockHttpRequest request = post("/api/systeminfo/notification");
        HttpResponse response = sendRequest(request);

        assertEquals(200, response.getStatusCode());
        validateByOpenAPI("get-systeminfo-notification", request, response);
    }

    @Test
    public void ログインしていない場合はWebSocketの接続URLを取得できない() throws Exception {
        loadCsrfToken();
        RestMockHttpRequest request = post("/api/systeminfo/notification");
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-systeminfo-notification", request, response);
    }
}
