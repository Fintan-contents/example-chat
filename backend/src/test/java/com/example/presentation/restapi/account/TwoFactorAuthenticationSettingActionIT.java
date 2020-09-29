package com.example.presentation.restapi.account;

import com.example.application.service.authentication.AuthenticationCodeGenerator;
import com.example.domain.model.account.SecretString;
import com.example.domain.model.authentication.AuthenticationCode;
import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TwoFactorAuthenticationSettingActionIT extends ExampleChatRestTestBase {

    @Test
    public void 二要素認証が有効かどうかを取得できる() throws Exception {
        login("user1@example.com", "pass123-");

        RestMockHttpRequest status2FARequest = get("/api/settings/2fa");
        HttpResponse status2FAResponse = sendRequest(status2FARequest);
        assertEquals(200, status2FAResponse.getStatusCode());
        JsonNode status2FARoot = new ObjectMapper().readTree(status2FAResponse.getBodyString());
        assertEquals("DISABLED", status2FARoot.get("status").asText());

        validateByOpenAPI("get-settings-2fa", status2FARequest, status2FAResponse);
    }

    @Test
    public void 二要素認証を有効にできる() throws Exception {
        login("user1@example.com", "pass123-");

        // 二要素認証を有効にできる
        RestMockHttpRequest enable2FARequest = post("/api/settings/2fa/enable");
        HttpResponse enable2FAResponse = sendRequest(enable2FARequest);
        assertEquals(200, enable2FAResponse.getStatusCode());

        JsonNode enable2FARoot = new ObjectMapper().readTree(enable2FAResponse.getBodyString());
        String secretString = enable2FARoot.get("secretString").asText();
        assertNotNull(secretString);

        validateByOpenAPI("post-settings-2fa-enable", enable2FARequest, enable2FAResponse);

        // 二要素認証をアクティベートできる
        AuthenticationCode code = new AuthenticationCodeGenerator().generate(new SecretString(secretString).toSecretKey());
        RestMockHttpRequest verify2FARequest = post("/api/settings/2fa/enable/verify").setContentType("application/json")
                .setBody(Map.of("code", code.value()));
        HttpResponse verify2FAResponse = sendRequest(verify2FARequest);
        assertEquals(204, verify2FAResponse.getStatusCode());

        validateByOpenAPI("post-settings-2fa-enable-verify", verify2FARequest, verify2FAResponse);

        // 二要素認証が有効になっている
        RestMockHttpRequest status2FARequest = get("/api/settings/2fa");
        HttpResponse status2FAResponse = sendRequest(status2FARequest);
        assertEquals(200, status2FAResponse.getStatusCode());
        JsonNode status2FARoot = new ObjectMapper().readTree(status2FAResponse.getBodyString());
        assertEquals("ENABLED", status2FARoot.get("status").asText());
    }

    @Test
    public void 二要素認証を無効にできる() throws Exception {
        二要素認証を有効にできる();

        // 二要素認証を無効にできる
        RestMockHttpRequest disable2FARequest = post("/api/settings/2fa/disable");
        HttpResponse disable2FAResponse = sendRequest(disable2FARequest);
        assertEquals(204, disable2FAResponse.getStatusCode());

        validateByOpenAPI("post-settings-2fa-disable",disable2FARequest, disable2FAResponse);

        // 二要素認証が無効になっている
        RestMockHttpRequest status2FARequest = get("/api/settings/2fa");
        HttpResponse status2FAResponse = sendRequest(status2FARequest);
        assertEquals(200, status2FAResponse.getStatusCode());
        JsonNode status2FARoot = new ObjectMapper().readTree(status2FAResponse.getBodyString());
        assertEquals("DISABLED", status2FARoot.get("status").asText());
    }

    @Test
    public void ログインしていない場合は二要素認証が有効かどうかを取得できない() {
        loadCsrfToken();

        RestMockHttpRequest status2FARequest = get("/api/settings/2fa");
        HttpResponse status2FAResponse = sendRequest(status2FARequest);
        assertEquals(403, status2FAResponse.getStatusCode());
    }

    @Test
    public void ログインしていない場合は二要素認証が有効にできない() {
        loadCsrfToken();

        RestMockHttpRequest enable2FARequest = post("/api/settings/2fa/enable");
        HttpResponse enable2FAResponse = sendRequest(enable2FARequest);
        assertEquals(403, enable2FAResponse.getStatusCode());
    }

    @Test
    public void ログインしていない場合は二要素認証を無効にできない() {
        loadCsrfToken();

        RestMockHttpRequest disable2FARequest = post("/api/settings/2fa/disable");
        HttpResponse disable2FAResponse = sendRequest(disable2FARequest);
        assertEquals(403, disable2FAResponse.getStatusCode());
    }

    @Test
    public void 不正なコードで二要素認証をアクティベートできない() throws Exception {
        login("user1@example.com", "pass123-");

        RestMockHttpRequest invalidRequest = post("/api/settings/2fa/enable/verify").setContentType("application/json")
                .setBody(Map.of("code", "XXXXXX"));
        HttpResponse invalidResponse = sendRequest(invalidRequest);
        assertEquals(403, invalidResponse.getStatusCode());

        // 項目として送信しないケース
        RestMockHttpRequest notSendRequest = post("/api/settings/2fa/enable/verify").setContentType("application/json");
        HttpResponse notSendResponse = sendRequest(notSendRequest);
        assertEquals(400, notSendResponse.getStatusCode());
    }
}
