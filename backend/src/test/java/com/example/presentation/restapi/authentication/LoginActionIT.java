package com.example.presentation.restapi.authentication;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LoginActionIT extends ExampleChatRestTestBase {

    @Test
    public void ログインができる() throws Exception {
        loadCsrfToken();

        String mailAddress = "user1@example.com";
        String password = "pass123-";

        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());
        JsonNode root = new ObjectMapper().readTree(response.getBodyString());
        assertEquals("COMPLETE", root.get("status").asText());

        validateByOpenAPI("post-login", request, response);
    }

    @Test
    public void 認証に失敗したら401エラーレスポンスを投げる() {
        loadCsrfToken();

        String mailAddress = "nodata@example.com";
        String password = "pass123-";

        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(401, response.getStatusCode());

        validateByOpenAPI("post-login", request, response);
    }

    @Test
    public void パスワードが間違っているとログインできない() throws Exception {
        loadCsrfToken();

        String mailAddress = "user1@example.com";
        String password = "mistake123-";

        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(401, response.getStatusCode());

        validateByOpenAPI("post-login", request, response);
    }

    @Test
    public void ログイン時のメールアドレスとして不正() {
        loadCsrfToken();

        String[] invalidMailAddresses = new String[] { "", " ", "noDomain" };
        String password = "pass123-";

        Arrays.stream(invalidMailAddresses).forEach(invalidMailaddress -> {
            RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                    .setBody(Map.of("mailAddress", invalidMailaddress, "password", password));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void ログイン時のパスワードとして不正() {
        loadCsrfToken();

        String[] invalidPasswords = new String[] { "", "       ", "1234567" };
        String mailAddress = "user1@example.com";

        Arrays.stream(invalidPasswords).forEach(invalidPassword -> {
            RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                    .setBody(Map.of("mailAddress", mailAddress, "password", invalidPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }
}
