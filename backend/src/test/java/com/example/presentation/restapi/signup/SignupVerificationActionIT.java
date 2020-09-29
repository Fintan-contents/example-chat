package com.example.presentation.restapi.signup;

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

public class SignupVerificationActionIT extends ExampleChatRestTestBase {

    MailHelper mailer = new MailHelper();

    @Test
    public void アカウントの本登録ができる() throws Exception {
        loadCsrfToken();

        String registerUserName = "testUser1";
        String registerMailAddress = "testUser1@example.com";
        String registerPassword = "password";

        mailer.clearMail();

        // アカウントを仮登録をする
        RestMockHttpRequest temporaryRegisterRequest = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("userName", registerUserName, "mailAddress", registerMailAddress, "password", registerPassword));
        HttpResponse temporaryRegisterResponse = sendRequest(temporaryRegisterRequest);
        assertEquals(204, temporaryRegisterResponse.getStatusCode());

        // メールを検索する
        List<MailHelper.Mail> mails = mailer.searchMail();
        Assertions.assertEquals(1, mails.size());
        String mailBody = mails.get(0).body;
        String userToken = mailBody.substring(mailBody.lastIndexOf("/") + 1);

        // アカウントを本登録をする
        RestMockHttpRequest registerRequest = post("/api/signup/verify").setContentType("application/json")
                .setBody(Map.of("userToken", userToken));
        HttpResponse registerResponse = sendRequest(registerRequest);
        assertEquals(204, registerResponse.getStatusCode());

        validateByOpenAPI("post-signup-verify", registerRequest, registerResponse);

        // 同じユーザー名でサインアップするとエラーになる
        HttpResponse conflictSignupResponse = sendRequest(temporaryRegisterRequest);
        assertEquals(409, conflictSignupResponse.getStatusCode());

        // 処理済みのトークンでもう一度本登録するとエラーになる
        HttpResponse conflictVerifyResponse = sendRequest(registerRequest);
        assertEquals(404, conflictVerifyResponse.getStatusCode());

        // 登録したアカウントでログインできること
        login(registerMailAddress, registerPassword);
    }

    @Test
    public void 既に存在する名前ではアカウントを本登録できない() throws Exception {
        loadCsrfToken();

        String registerUserName = "testUser1";

        mailer.clearMail();

        // 2つ同じユーザー名でアカウントを仮登録して、それぞれ本登録へ臨む
        RestMockHttpRequest temporaryRegisterRequest = post("/api/signup")
                .setContentType("application/json")
                .setBody(Map.of("userName", registerUserName, "mailAddress", "test2@example.com", "password", "pass123-"));
        sendRequest(temporaryRegisterRequest);

        RestMockHttpRequest temporaryRegisterRequest2 = post("/api/signup")
                .setContentType("application/json")
                .setBody(Map.of("userName", registerUserName, "mailAddress", "test1@example.com", "password", "pass123-"));
        sendRequest(temporaryRegisterRequest2);

        // メールを検索する
        List<MailHelper.Mail> mails = mailer.searchMail();
        Assertions.assertEquals(2, mails.size());
        String mailBody1 = mails.get(0).body;
        String userToken1 = mailBody1.substring(mailBody1.lastIndexOf("/") + 1);
        String mailBody2 = mails.get(1).body;
        String userToken2 = mailBody2.substring(mailBody2.lastIndexOf("/") + 1);

        // アカウントを本登録をする
        RestMockHttpRequest registerRequest1 = post("/api/signup/verify")
                .setContentType("application/json")
                .setBody(Map.of("userToken", userToken1));
        HttpResponse registerResponse1 = sendRequest(registerRequest1);
        assertEquals(204, registerResponse1.getStatusCode());

        RestMockHttpRequest registerRequest2 = post("/api/signup/verify")
                .setContentType("application/json")
                .setBody(Map.of("userToken", userToken2));
        HttpResponse registerResponse2 = sendRequest(registerRequest2);
        // 同じユーザー名で既に本登録済みのためエラーになる
        assertEquals(409, registerResponse2.getStatusCode());

        validateByOpenAPI("post-signup-verify", registerRequest2, registerResponse2);
    }

    @Test
    public void アカウント仮登録のユーザー名が不正() {
        loadCsrfToken();

        String registerMailAddress = "testUser1@example.com";
        String registerPassword = "password";
        String[] invalidUserNames = new String[] { "", " " };

        Arrays.stream(invalidUserNames).forEach(invalidUserName -> {
            RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                    .setBody(Map.of("userName", invalidUserName, "mailAddress", registerMailAddress, "password", registerPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("mailAddress", registerMailAddress, "password", registerPassword));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void アカウント仮登録のメールアドレスが不正() {
        loadCsrfToken();

        String registerUserName = "testUser1";
        String registerPassword = "password";
        String[] invalidMailAddresses = new String[] { "", " ", "noDomain" };

        Arrays.stream(invalidMailAddresses).forEach(invalidMailAddress -> {
            RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                    .setBody(Map.of("userName", registerUserName, "mailAddress", invalidMailAddress, "password", registerPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("userName", registerUserName, "password", registerPassword));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void アカウント仮登録のパスワードが不正() {
        loadCsrfToken();

        String registerUserName = "testUser1";
        String registerMailAddress = "testUser1@example.com";
        String[] invalidPasswords = new String[] { "", "        ", "1234567" };

        Arrays.stream(invalidPasswords).forEach(invalidPassword -> {
            RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                    .setBody(Map.of("userName", registerUserName, "mailAddress", registerMailAddress, "password", invalidPassword));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/signup").setContentType("application/json")
                .setBody(Map.of("userName", registerUserName, "mailAddress", registerMailAddress));
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void アカウント本登録のユーザートークンとして不正() {
        loadCsrfToken();

        String[] invalidUserTokens = new String[] { "", "        " };

        Arrays.stream(invalidUserTokens).forEach(invalidUserToken -> {
            RestMockHttpRequest request = post("/api/signup/verify").setContentType("application/json")
                    .setBody(Map.of("userToken", invalidUserToken));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/signup/verify").setContentType("application/json");
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }
}