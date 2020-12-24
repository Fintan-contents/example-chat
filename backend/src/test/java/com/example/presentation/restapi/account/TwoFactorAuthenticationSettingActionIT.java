package com.example.presentation.restapi.account;

import com.example.application.service.authentication.AuthenticationCodeGenerator;
import com.example.domain.model.account.SecretString;
import com.example.domain.model.authentication.AuthenticationCode;
import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TwoFactorAuthenticationSettingActionIT extends ExampleChatRestTestBase {

    @Test
    public void 二要素認証が有効かどうかを取得できる() throws Exception {
        login("user1@example.com", "pass123-");

        RestMockHttpRequest status2FARequest = get("/api/settings/2fa");
        HttpResponse status2FAResponse = sendRequest(status2FARequest);

        assertEquals(200, status2FAResponse.getStatusCode());
        JsonAssert.with(status2FAResponse.getBodyString())
                .assertEquals("$.status", "DISABLED");
        validateByOpenAPI("get-settings-2fa", status2FARequest, status2FAResponse);
    }

    @Test
    public void 二要素認証を有効にできる() throws Exception {
        login("user1@example.com", "pass123-");

        // 二要素認証を有効にできる
        RestMockHttpRequest enable2FARequest = post("/api/settings/2fa/enable");
        HttpResponse enable2FAResponse = sendRequest(enable2FARequest);

        assertEquals(200, enable2FAResponse.getStatusCode());
        JsonAssert.with(enable2FAResponse.getBodyString())
                .assertNotNull("$.secretString");
        validateByOpenAPI("post-settings-2fa-enable", enable2FARequest, enable2FAResponse);

        // 二要素認証をアクティベートできる
        JsonNode enable2FARoot = new ObjectMapper().readTree(enable2FAResponse.getBodyString());
        String secretString = enable2FARoot.get("secretString").asText();
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
        JsonAssert.with(status2FAResponse.getBodyString())
                .assertEquals("$.status", "ENABLED");
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
        JsonAssert.with(status2FAResponse.getBodyString())
                .assertEquals("$.status", "DISABLED");
    }

    @Test
    public void ログインしていない場合は二要素認証が有効かどうかを取得できない() {
        loadCsrfToken();

        RestMockHttpRequest status2FARequest = get("/api/settings/2fa");
        HttpResponse status2FAResponse = sendRequest(status2FARequest);

        assertEquals(403, status2FAResponse.getStatusCode());
        JsonAssert.with(status2FAResponse.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-settings-2fa", status2FARequest, status2FAResponse);
    }

    @Test
    public void ログインしていない場合は二要素認証が有効にできない() {
        loadCsrfToken();

        RestMockHttpRequest enable2FARequest = post("/api/settings/2fa/enable");
        HttpResponse enable2FAResponse = sendRequest(enable2FARequest);

        assertEquals(403, enable2FAResponse.getStatusCode());
        JsonAssert.with(enable2FAResponse.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("post-settings-2fa-enable", enable2FARequest, enable2FAResponse);
    }

    @Test
    public void ログインしていない場合は二要素認証を無効にできない() {
        loadCsrfToken();

        RestMockHttpRequest disable2FARequest = post("/api/settings/2fa/disable");
        HttpResponse disable2FAResponse = sendRequest(disable2FARequest);

        assertEquals(403, disable2FAResponse.getStatusCode());
        JsonAssert.with(disable2FAResponse.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("post-settings-2fa-disable", disable2FARequest, disable2FAResponse);
    }

    @Test
    public void ログインしていない場合は二要素認証有効化をアクティベートできない() {
        loadCsrfToken();

        RestMockHttpRequest enable2FARequest = post("/api/settings/2fa/enable/verify")
                .setContentType("application/json")
                .setBody(Map.of("code", "xxxxxx"));
        HttpResponse enable2FAResponse = sendRequest(enable2FARequest);

        assertEquals(403, enable2FAResponse.getStatusCode());
        JsonAssert.with(enable2FAResponse.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("post-settings-2fa-enable-verify", enable2FARequest, enable2FAResponse);
    }

    @Test
    public void 二要素認証の有効化待ちでなければアクティベートできない() throws Exception {
        login("user1@example.com", "pass123-");

        RestMockHttpRequest verify2FARequest = post("/api/settings/2fa/enable/verify").setContentType("application/json")
                .setBody(Map.of("code", "xxxxxx"));
        HttpResponse verify2FAResponse = sendRequest(verify2FARequest);

        assertEquals(403, verify2FAResponse.getStatusCode());
        validateByOpenAPI("post-settings-2fa-enable-verify", verify2FARequest, verify2FAResponse);
    }

    @Test
    public void 認証コードが一致しなければ二要素認証有効化をアクティベートできない() throws Exception {
        login("user1@example.com", "pass123-");

        // 二要素認証を有効にする
        RestMockHttpRequest enable2FARequest = post("/api/settings/2fa/enable");
        HttpResponse enable2FAResponse = sendRequest(enable2FARequest);
        assertEquals(200, enable2FAResponse.getStatusCode());

        // 認証コードが間違っていれば有効化をアクティベートできない
        RestMockHttpRequest verify2FARequest = post("/api/settings/2fa/enable/verify").setContentType("application/json")
                .setBody(Map.of("code", "xxxxxx"));
        HttpResponse verify2FAResponse = sendRequest(verify2FARequest);

        assertEquals(401, verify2FAResponse.getStatusCode());
        validateByOpenAPI("post-settings-2fa-enable-verify", verify2FARequest, verify2FAResponse);
    }

    @Test
    public void 不正な認証コードで二要素認証有効化をアクティベートできない() throws Exception {
        login("user1@example.com", "pass123-");

        String[] codeList = new String[] { "", " " };
        Arrays.stream(codeList).forEach(code -> {
            RestMockHttpRequest invalidRequest = post("/api/settings/2fa/enable/verify")
                    .setContentType("application/json")
                    .setBody(Map.of("code", code));
            HttpResponse invalidResponse = sendRequest(invalidRequest);

            assertEquals(400, invalidResponse.getStatusCode());
            JsonAssert.with(invalidResponse.getBodyString())
                    .assertEquals("$.code", "request");
            validateByOpenAPI("post-settings-2fa-enable-verify", invalidRequest, invalidResponse);
        });

        // 項目として送信しないケース
        RestMockHttpRequest notSendRequest = post("/api/settings/2fa/enable/verify")
                .setContentType("application/json");
        HttpResponse notSendResponse = sendRequest(notSendRequest);

        assertEquals(400, notSendResponse.getStatusCode());
        JsonAssert.with(notSendResponse.getBodyString())
                .assertEquals("$.code", "request");
        validateByOpenAPI("post-settings-2fa-enable-verify", notSendResponse);
    }
}
