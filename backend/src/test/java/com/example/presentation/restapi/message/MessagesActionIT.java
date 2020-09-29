package com.example.presentation.restapi.message;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class MessagesActionIT extends ExampleChatRestTestBase {

    @Test
    public void メッセージの一覧を取得できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/messages");
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());

        JSONAssert.assertEquals("メッセージ一覧取得", readTextResource("response.json"),
                response.getBodyString(), JSONCompareMode.LENIENT);

        validateByOpenAPI("get-channels-channelId-messages", request, response);
    }

    @Test
    public void 一定数を超えたメッセージの一覧には次ページのキーが含まれる() throws Exception {
        login("user1@example.com", "pass123-");

        Integer channelId = 1;
        for (int i = 0; i < 101; i++) {
            RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                    .setContentType("application/json")
                    .setBody(Map.of("text", "testmessage" + 1));
            HttpResponse response = sendRequest(request);
            assertEquals(204, response.getStatusCode());
        }

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/messages");
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());

        JsonAssert
                .with(response.getBodyString())
                .assertNotNull("$.nextMessageId");

        validateByOpenAPI("get-channels-channelId-messages", request, response);
    }

    @Test
    public void メッセージの一覧を最新のメッセージIDを指定して取得できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        Integer messageId = 1;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/messages?nextMessageId=" + messageId);
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());

        JsonAssert.with(response.getBodyString())
                .assertThat("$.messages", Matchers.hasSize(1))
                .assertEquals("$.messages[0].text", "#generalに参加しました。");

        validateByOpenAPI("get-channels-channelId-messages", request, response);
    }

    @Test
    public void ログインしていない場合はメッセージの一覧を取得できない() {
        Integer channelId = 1;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/messages");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 参加していないチャンネルのメッセージ一覧は取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;
        Integer messageId = Integer.MAX_VALUE;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/messages");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 存在しないチャンネルのメッセージ一覧は取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/messages");
        HttpResponse response = sendRequest(request);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void メッセージを投稿できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        String text = UUID.randomUUID().toString();
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                .setContentType("application/json")
                .setBody(Map.of("text", text));
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        validateByOpenAPI("post-channels-channelId-messages", request, response);

        RestMockHttpRequest listRequest = get("/api/channels/" + channelId + "/messages");
        HttpResponse listResponse = sendRequest(listRequest);
        assertEquals(200, listResponse.getStatusCode());

        JsonAssert.with(listResponse.getBodyString())
                .assertEquals("$.messages[0].text", text);
    }

    @Test
    public void メッセージを投稿すると通知される() throws Exception {
        login("user1@example.com", "pass123-");
        BlockingDeque<Map<String, Object>> messageQueue = connectNotificationQueue();

        Integer channelId = 1;
        String text = UUID.randomUUID().toString();
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                .setContentType("application/json")
                .setBody(Map.of("text", text));
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        Map<String, Object> receivedMessage = messageQueue.poll(5, TimeUnit.SECONDS);

        assertEquals("message", receivedMessage.get("type"));

        Map<String, String> payload = (Map<String, String>) receivedMessage.get("payload");
        assertNotNull(payload.get("messageId"));
        assertEquals(text, payload.get("text"));
    }

    @Test
    public void 投稿するメッセージが不正() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;

        String[] invalidTexts = new String[] { "", " " };

        Arrays.stream(invalidTexts).forEach(invalidText -> {
            RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                    .setContentType("application/json")
                    .setBody(Map.of("text", invalidText));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                .setContentType("application/json");
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void ログインしていない場合はメッセージを投稿できない() {
        loadCsrfToken();
        Integer channelId = 1;
        String text = UUID.randomUUID().toString();
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                .setContentType("application/json")
                .setBody(Map.of("text", text));
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 参加していないチャンネルにはメッセージを投稿できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;
        String text = UUID.randomUUID().toString();
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                .setContentType("application/json")
                .setBody(Map.of("text", text));
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 存在しないチャンネルにはメッセージを投稿できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        String text = UUID.randomUUID().toString();
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/messages")
                .setContentType("application/json")
                .setBody(Map.of("text", text));
        HttpResponse response = sendRequest(request);
        assertEquals(404, response.getStatusCode());
    }
}
