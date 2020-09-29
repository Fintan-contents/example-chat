package com.example.presentation.restapi.account;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyAccountActionIT extends ExampleChatRestTestBase {

    @Test
    public void アカウント情報を取得できる() throws Exception {
        login("user1@example.com", "pass123-");

        RestMockHttpRequest request = get("/api/accounts/me");
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());

        JsonAssert.with(response.getBodyString())
                .assertEquals("$.accountId", 1)
                .assertEquals("$.userName", "user1")
                .assertEquals("$.mailAddress", "user1@example.com");

        validateByOpenAPI("get-accouunts", request, response);
    }

    @Test
    public void ログインしていない場合はアカウント情報を取得できない() {
        loadCsrfToken();
        RestMockHttpRequest request = get("/api/accounts/me");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }
}
