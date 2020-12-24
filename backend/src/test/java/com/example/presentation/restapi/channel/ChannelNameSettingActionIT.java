package com.example.presentation.restapi.channel;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ChannelNameSettingActionIT extends ExampleChatRestTestBase {

    @Test
    public void チャンネル名を変更できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;

        RestMockHttpRequest request = put("/api/channels/"  + channelId + "/settings/name")
                .setContentType("application/json")
                .setBody(Map.of("channelName", "changeNameTest"));
        HttpResponse response = sendRequest(request);

        assertEquals(204, response.getStatusCode());
        validateByOpenAPI("put-channels-channelId-settings-name", request, response);
    }

    @Test
    public void チャンネル名を変更すると通知される() throws Exception {
        login("user1@example.com", "pass123-");
        BlockingDeque<Map<String, Object>> receivedMessageQueue = connectNotificationQueue();
        Integer channelId = 1;

        RestMockHttpRequest request = put("/api/channels/" + channelId + "/settings/name")
                .setContentType("application/json")
                .setBody(Map.of("channelName", "changeNameTest"));
        HttpResponse response = sendRequest(request);

        assertEquals(204, response.getStatusCode());

        // チャンネル名を変更した旨のメッセージ投稿も行われるため、type=messageの通知も来る。
        // このテストケースではチャンネル名変更の通知をテストしたいため、type=channelNameChanged以外は捨てる。
        Map<String, Object> receivedMessage;
        do {
            receivedMessage = receivedMessageQueue.poll(5, TimeUnit.SECONDS);
        } while (receivedMessage.get("type").equals("channelNameChanged") == false);
        Map<String, Object> payload = (Map<String, Object>) receivedMessage.get("payload");

        assertEquals("general", payload.get("oldChannelName"));
        assertEquals("changeNameTest", payload.get("newChannelName"));
    }

    @Test
    public void 既に存在するチャンネル名へは変更できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;

        RestMockHttpRequest request = put("/api/channels/" + channelId + "/settings/name")
                .setContentType("application/json")
                .setBody(Map.of("channelName", "channel1"));
        HttpResponse response = sendRequest(request);

        assertEquals(409, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.conflict");
        validateByOpenAPI("put-channels-channelId-settings-name", request, response);
    }

    @Test
    public void チャンネル名を変更のチャンネル名が不正() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        String[] invalidChannelNames = new String[] { "", " " };

        Arrays.stream(invalidChannelNames).forEach(invalidChannelName -> {
            RestMockHttpRequest request = put("/api/channels/"  + channelId + "/settings/name")
                    .setContentType("application/json")
                    .setBody(Map.of("channelName", invalidChannelName));
            HttpResponse response = sendRequest(request);

            assertEquals(400, response.getStatusCode());
            JsonAssert.with(response.getBodyString())
                    .assertEquals("$.code", "request");
            validateByOpenAPI("put-channels-channelId-settings-name", request, response);
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = put("/api/channels/"  + channelId + "/settings/name").setContentType("application/json");
        HttpResponse response = sendRequest(request);

        assertEquals(400, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "request");
        validateByOpenAPI("put-channels-channelId-settings-name", response);
    }

    @Test
    public void ログインしていない場合はチャンネル名を変更できない() {
        loadCsrfToken();
        Integer channelId = 1;

        RestMockHttpRequest request = put("/api/channels/"  + channelId + "/settings/name")
                .setContentType("application/json")
                .setBody(Map.of("channelName", "changeNameTest"));
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("put-channels-channelId-settings-name", request, response);
    }

    @Test
    public void 参加していないチャンネルでチャンネル名を変更できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;

        RestMockHttpRequest request = put("/api/channels/"  + channelId + "/settings/name")
                .setContentType("application/json")
                .setBody(Map.of("channelName", "changeNameTest"));
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("put-channels-channelId-settings-name", request, response);
    }

    @Test
    public void 存在しないチャンネルでチャンネル名を変更できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        RestMockHttpRequest request = put("/api/channels/"  + channelId + "/settings/name")
                .setContentType("application/json")
                .setBody(Map.of("channelName", "changeNameTest"));
        HttpResponse response = sendRequest(request);

        assertEquals(404, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.notfound");
        validateByOpenAPI("put-channels-channelId-settings-name", request, response);
    }

    @Test
    public void ChatBotのチャンネル名を変更できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 2;

        RestMockHttpRequest request = put("/api/channels/"  + channelId + "/settings/name")
                .setContentType("application/json")
                .setBody(Map.of("channelName", "changeNameTest"));
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("put-channels-channelId-settings-name", request, response);
    }
}
