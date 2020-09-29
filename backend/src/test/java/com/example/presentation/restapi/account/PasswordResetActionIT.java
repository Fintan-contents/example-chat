package com.example.presentation.restapi.account;

import com.example.MailHelper;
import com.example.presentation.restapi.ExampleChatRestTestBase;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PasswordResetActionIT extends ExampleChatRestTestBase {

    MailHelper mailer = new MailHelper();

    @Test
    public void パスワードリセットができること() throws Exception{
        loadCsrfToken();

        String mailAddress = "user1@example.com";

        mailer.clearMail();

        // パスワードリセットを依頼する
        RestMockHttpRequest resetRequest = post("/api/reset_password")
                .setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress));
        HttpResponse resetResponse = sendRequest(resetRequest);
        assertEquals(204, resetResponse.getStatusCode());

        validateByOpenAPI("post-reset_password", resetRequest, resetResponse);

        // メールを検索する
        List<MailHelper.Mail> mails = mailer.searchMail();
        Assertions.assertEquals(1, mails.size());
        String mailBody = mails.get(0).body;
        String token = mailBody.substring(mailBody.lastIndexOf("/") + 1);

        // パスワードリセットの認証トークンを検証する
        RestMockHttpRequest verifyRequest = post("/api/reset_password/verify").setContentType("application/json")
                .setBody(Map.of("token", token));
        HttpResponse verifyResponse = sendRequest(verifyRequest);
        assertEquals(204, verifyResponse.getStatusCode());

        validateByOpenAPI("post-reset_password-verify", verifyRequest, verifyResponse);

        // 新パスワードを設定する
        String newPassword = "newPass-";
        RestMockHttpRequest passwordNewRequest = post("/api/reset_password/new").setContentType("application/json")
                .setBody(Map.of("token", token, "newPassword", newPassword));
        HttpResponse passwordNewResponse = sendRequest(passwordNewRequest);
        assertEquals(204, passwordNewResponse.getStatusCode());

        validateByOpenAPI("post-reset_password-new", passwordNewRequest, passwordNewResponse);

        // リセット後の新パスワードでログインできること
        login(mailAddress, newPassword);
    }

    @Test
    public void パスワードリセット依頼のメールアドレスが不正() {
        loadCsrfToken();

        String[] invalidMailAddresses = new String[] { "", " ", "noDomain" };

        Arrays.stream(invalidMailAddresses).forEach(invalidMailAddress -> {
            RestMockHttpRequest request = post("/api/reset_password")
                    .setContentType("application/json")
                    .setBody(Map.of("mailAddress", invalidMailAddress));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/reset_password")
                .setContentType("application/json");
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void パスワードリセットの認証トークンが不正() {
        loadCsrfToken();

        String[] invalidUserTokens = new String[] { "", "        " };

        Arrays.stream(invalidUserTokens).forEach(invalidUserToken -> {
            RestMockHttpRequest request = post("/api/reset_password/verify").setContentType("application/json")
                    .setBody(Map.of("token", invalidUserToken));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/reset_password/verify").setContentType("application/json");
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void 新パスワード設定のトークンが不正() {
        loadCsrfToken();

        String newPassword = "newPass-";
        String[] invalidUserTokens = new String[] { "", "        " };

        Arrays.stream(invalidUserTokens).forEach(invalidUserToken -> {
            RestMockHttpRequest request = post("/api/reset_password/new").setContentType("application/json")
                    .setBody(Map.of("token", invalidUserToken, "newPassword", newPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/reset_password/new").setContentType("application/json")
                .setBody(Map.of("newPassword", newPassword));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void 新パスワード設定のパスワードが不正() {
        loadCsrfToken();

        String token = "XXXXXXXXXXX";
        String[] invalidPasswords = new String[] { "", "        ", "1234567" };

        Arrays.stream(invalidPasswords).forEach(invalidPassword -> {
            RestMockHttpRequest request = post("/api/reset_password/new").setContentType("application/json")
                    .setBody(Map.of("token", token, "newPassword", invalidPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/reset_password/new").setContentType("application/json")
                .setBody(Map.of("token", token));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }
}
