package com.example.presentation.restapi.account;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MyAccountDeletionActionIT extends ExampleChatRestTestBase {

    @Test
    public void アカウントを削除できる() throws Exception {
        String password = "pass123-";
        login("user1@example.com", password);

        RestMockHttpRequest request = post("/api/accounts/me/delete")
                .setContentType("application/json")
                .setBody(Map.of("password", password));

        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        validateByOpenAPI("delete-accounts-me-delete", request, response);
    }

    @Test
    public void チャンネルメンバーだけのチャンネルがあるアカウントでも削除できる() throws Exception {
        String password = "pass123-";
        login("user2@example.com", password);

        RestMockHttpRequest request = post("/api/accounts/me/delete")
                .setContentType("application/json")
                .setBody(Map.of("password", password));

        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void パスワードが正しくない場合はアカウントを削除できない() throws Exception {
        String password = "pass123-";
        String mistakePassword = "mistake123-";
        login("user1@example.com", password);

        RestMockHttpRequest request = post("/api/accounts/me/delete")
                .setContentType("application/json")
                .setBody(Map.of("password", mistakePassword));

        HttpResponse response = sendRequest(request);
        assertEquals(401, response.getStatusCode());

        validateByOpenAPI("delete-accounts-me-delete", request, response);
    }

    @Test
    public void ログインしていない場合はアカウントを削除できない() {
        loadCsrfToken();
        RestMockHttpRequest request = post("/api/accounts/me/delete")
                .setContentType("application/json")
                .setBody(Map.of("password", "pass123-"));

        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }
}
