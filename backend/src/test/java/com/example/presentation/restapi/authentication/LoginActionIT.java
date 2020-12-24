package com.example.presentation.restapi.authentication;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
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
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.status", "COMPLETE");
        validateByOpenAPI("post-login", request, response);
    }

    @Test
    public void メールアドレスが登録されていなければ認証に失敗する() {
        loadCsrfToken();

        String mailAddress = "nodata@example.com";
        String password = "pass123-";

        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);

        assertEquals(401, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "authentication.failed");
        validateByOpenAPI("post-login", request, response);
    }

    @Test
    public void パスワードが一致しなければ認証に失敗する() throws Exception {
        loadCsrfToken();

        String mailAddress = "user1@example.com";
        String password = "mistake123-";

        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);

        assertEquals(401, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "authentication.failed");
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
            JsonAssert.with(response.getBodyString())
                    .assertEquals("$.code", "request");
            validateByOpenAPI("post-login", request, response);
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("password", password));
        HttpResponse response = sendRequest(request);

        assertEquals(400, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "request");
        validateByOpenAPI("post-login", response);
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
            JsonAssert.with(response.getBodyString())
                    .assertEquals("$.code", "request");
            validateByOpenAPI("post-login", request, response);
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress));
        HttpResponse response = sendRequest(request);

        assertEquals(400, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "request");
        validateByOpenAPI("post-login", response);
    }

    @Test
    public void ログアウトできる() {
        loadCsrfToken();

        String mailAddress = "user1@example.com";
        String password = "pass123-";

        RestMockHttpRequest loginRequest = post("/api/login").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse loginResponse = sendRequest(loginRequest);
        assertEquals(200, loginResponse.getStatusCode());

        // ログインするとセッションがリフレッシュされるので再度取得する
        loadCsrfToken();

        RestMockHttpRequest request = post("/api/logout");
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());
        validateByOpenAPI("post-logout", request, response);
    }

    @Test
    public void ログインしていなければログアウトでエラーになる() {
        loadCsrfToken();

        RestMockHttpRequest request = post("/api/logout");
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        validateByOpenAPI("post-logout", response);
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("post-logout", request, response);
    }

}
