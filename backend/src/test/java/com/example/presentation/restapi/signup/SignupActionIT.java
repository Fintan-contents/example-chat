package com.example.presentation.restapi.signup;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SignupActionIT extends ExampleChatRestTestBase {

    @Test
    public void アカウントを仮登録できる() {
        loadCsrfToken();

        String userName = "testUser1";
        String mailAddress = "testUser1@example.com";
        String password = "password";

        RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("userName", userName, "mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        validateByOpenAPI("post-signup", request, response);
    }

    @Test
    public void アカウント仮登録のユーザー名として不正() {
        loadCsrfToken();

        String[] invalidUserNames = new String[] { "", " " };
        String mailAddress = "testUser1@example.com";
        String password = "pass123-";

        Arrays.stream(invalidUserNames).forEach(invalidUserName -> {
            RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                    .setBody(Map.of("userName", invalidUserName, "mailAddress", mailAddress, "password", password));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void アカウント仮登録のメールアドレスとして不正() {
        loadCsrfToken();

        String userName = "testUser1";
        String[] invalidMailAddresses = new String[] { "", " ", "noDomain" };
        String password = "pass123-";

        Arrays.stream(invalidMailAddresses).forEach(invalidMailaddress -> {
            RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                    .setBody(Map.of("userName", userName, "mailAddress", invalidMailaddress, "password", password));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("userName", userName, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void アカウント仮登録のパスワードとして不正() {
        loadCsrfToken();

        String userName = "testUser1";
        String mailAddress = "testUser1@example.com";
        String[] invalidPasswords = new String[] { "", "       ", "1234567" };

        Arrays.stream(invalidPasswords).forEach(invalidPassword -> {
            RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                    .setBody(Map.of("userName", userName, "mailAddress", mailAddress, "password", invalidPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("userName", userName, "mailAddress", mailAddress));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }
}