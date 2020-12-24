package com.example.presentation.restapi.account;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PasswordSettingActionIT extends ExampleChatRestTestBase {

    @Test
    public void パスワードを変更できる() throws Exception {
        String mailAddress = "user1@example.com";
        String password = "pass123-";
        String newPassword = "newPass-";

        login(mailAddress, password);

        RestMockHttpRequest request = put("/api/settings/password")
                .setContentType("application/json")
                .setBody(Map.of("password", password, "newPassword", newPassword));
        HttpResponse response = sendRequest(request);

        assertEquals(204, response.getStatusCode());
        validateByOpenAPI("put-settings-password", request, response);

        // 新しいパスワードでログインできること
        logout();
        login(mailAddress, newPassword);
    }

    @Test
    public void 現在のパスワードが間違っている場合はパスワードを変更できない() throws Exception {
        String mailAddress = "user1@example.com";
        String password = "pass123-";
        String mistakePassword = "mistake123-";
        String newPassword = "newPass-";

        login(mailAddress, password);

        RestMockHttpRequest request = put("/api/settings/password")
                .setContentType("application/json")
                .setBody(Map.of("password", mistakePassword, "newPassword", newPassword));
        HttpResponse response = sendRequest(request);

        assertEquals(401, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "authentication.failed");
        validateByOpenAPI("put-settings-password", request, response);
    }

    @Test
    public void ログインしていない場合はパスワードを変更できない() {
        loadCsrfToken();

        RestMockHttpRequest request = put("/api/settings/password")
                .setContentType("application/json")
                .setBody(Map.of("password", "pass123-", "newPassword", "newPass-"));
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("put-settings-password", request, response);
    }

    @Test
    public void パスワード変更のパスワードが不正() throws Exception {
        login("user1@example.com", "pass123-");

        String[] invalidPasswords = new String[] { "", "        ", "1234567" };
        String newPassword = "12345678";

        Arrays.stream(invalidPasswords).forEach(invalidPassword -> {
            RestMockHttpRequest request = put("/api/settings/password")
                    .setContentType("application/json")
                    .setBody(Map.of("password", invalidPassword, "newPassword", newPassword));
            HttpResponse response = sendRequest(request);

            assertEquals(400, response.getStatusCode());
            JsonAssert.with(response.getBodyString())
                    .assertEquals("$.code", "request");
            validateByOpenAPI("put-settings-password", response);
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = put("/api/settings/password")
                .setContentType("application/json")
                .setBody(Map.of("newPassword", newPassword));
        HttpResponse response = sendRequest(request);

        assertEquals(400, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "request");
        validateByOpenAPI("put-settings-password", response);
    }

    @Test
    public void パスワード変更の新パスワードが不正() throws Exception {
        login("user1@example.com", "pass123-");

        String password = "12345678";
        String[] invalidNewPasswords = new String[] { "", "        ", "1234567" };

        Arrays.stream(invalidNewPasswords).forEach(invalidNewPassword -> {
            RestMockHttpRequest request = put("/api/settings/password")
                    .setContentType("application/json")
                    .setBody(Map.of("password", password, "newPassword", invalidNewPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = put("/api/settings/password")
                .setContentType("application/json")
                .setBody(Map.of("password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }
}
