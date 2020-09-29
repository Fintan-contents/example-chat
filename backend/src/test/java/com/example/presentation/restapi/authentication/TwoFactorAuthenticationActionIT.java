package com.example.presentation.restapi.authentication;

import com.example.application.service.authentication.AuthenticationCodeGenerator;
import com.example.domain.model.account.SecretKey;
import com.example.domain.model.authentication.AuthenticationCode;
import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Base64;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TwoFactorAuthenticationActionIT extends ExampleChatRestTestBase {

    @Test
    public void 二要素認証でログインできる() throws Exception {
        String mailAddress = "2FAUser@example.com";
        String password = "pass123-";

        // ログインする
        loadCsrfToken();
        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());
        JsonNode root = new ObjectMapper().readTree(response.getBodyString());
        assertEquals("WAITING_2FA", root.get("status").asText());

        // PostgreSQLのバイナリ文字列関数が「base64、hex、escape」しか対応していないため、テストデータ投入時は文字列をBase64でデコードしている
        // テストデータと一致させるため、ここでも同様の方式にする
        // https://www.postgresql.jp/document/11/html/functions-binarystring.html
        byte[] secretByte = Base64.getDecoder().decode("12345678901234567890123456789012");
        AuthenticationCode code = new AuthenticationCodeGenerator().generate(new SecretKey(secretByte));

        // 二要素認証でログインできる
        RestMockHttpRequest login2FARequest = post("/api/2fa").setContentType("application/json")
                .setBody(Map.of("code", code.value()));
        HttpResponse login2FAResponse = sendRequest(login2FARequest);
        assertEquals(204, login2FAResponse.getStatusCode());

        validateByOpenAPI("post-2fa", login2FARequest, login2FAResponse);
    }

    @Test
    public void 二要素認証のコードが不正() throws Exception {
        String mailAddress = "2FAUser@example.com";
        String password = "pass123-";

        // ログインする
        loadCsrfToken();
        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());
        JsonNode root = new ObjectMapper().readTree(response.getBodyString());
        assertEquals("WAITING_2FA", root.get("status").asText());

        // 不正なコードで二要素認証を行う
        RestMockHttpRequest login2FARequest = post("/api/2fa").setContentType("application/json")
                .setBody(Map.of("code", "XXXXXX"));
        HttpResponse login2FAResponse = sendRequest(login2FARequest);
        assertEquals(401, login2FAResponse.getStatusCode());

        // 項目として送信しないケース
        RestMockHttpRequest notSendRequest = post("/api/2fa").setContentType("application/json");
        HttpResponse notSendResponse = sendRequest(notSendRequest);
        assertEquals(400, notSendResponse.getStatusCode());
    }

    @Test
    public void 二要素認証が無効な場合はエラー() throws Exception {
        String mailAddress = "user1@example.com";
        String password = "pass123-";

        // ログインする
        loadCsrfToken();
        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());

        byte[] secretByte = Base64.getDecoder().decode("12345678901234567890123456789012");
        AuthenticationCode code = new AuthenticationCodeGenerator().generate(new SecretKey(secretByte));

        // 二要素認証はエラーになる
        RestMockHttpRequest login2FARequest = post("/api/2fa").setContentType("application/json")
                .setBody(Map.of("code", code.value()));
        HttpResponse login2FAResponse = sendRequest(login2FARequest);
        assertEquals(400, login2FAResponse.getStatusCode());

        validateByOpenAPI("post-2fa", login2FARequest, login2FAResponse);
    }
 }
