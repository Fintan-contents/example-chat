package com.example.presentation.restapi.channel;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ChannelMembersActionIT extends ExampleChatRestTestBase {

    @Test
    public void チャンネルの参加者一覧を取得できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/members");
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());

        JsonAssert.with(response.getBodyString())
                .assertThat("$", Matchers.hasSize(2))
                .assertEquals("$[0].id", 1)
                .assertEquals("$[0].name", "user1")
                .assertEquals("$[0].isOwner", true)
                .assertEquals("$[1].id", 2)
                .assertEquals("$[1].name", "user2")
                .assertEquals("$[1].isOwner", false);

        validateByOpenAPI("get-channels-channelId-members", request, response);
    }

    @Test
    public void ログインしていない場合はチャンネルの参加者を取得できない() {
        Integer channelId = 1;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/members");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 参加していないチャンネルの参加者は取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/members");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 存在しないチャンネルの参加者は取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        RestMockHttpRequest request = get("/api/channels/" + channelId + "/members");
        HttpResponse response = sendRequest(request);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void アカウントを招待できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        RestMockHttpRequest request = post("/api/channels/"  + channelId + "/members")
                .setContentType("application/json")
                .setBody(Map.of("accountId", 10000));
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        validateByOpenAPI("post-channels-channelId-members", request, response);
    }

    @Test
    public void アカウント招待のアカウントIDが不正() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        String[] invalidAccountIds = new String[] { "", " " };

        Arrays.stream(invalidAccountIds).forEach(invalidAccountId -> {
            RestMockHttpRequest request = post("/api/channels/"  + channelId + "/members")
                    .setContentType("application/json")
                    .setBody(Map.of("accountId", invalidAccountId));
            HttpResponse response = sendRequest(request);
            assertEquals(400, response.getStatusCode());
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/channels/"  + channelId + "/members").setContentType("application/json");
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void ログインしていない場合はアカウントを招待できない() {
        loadCsrfToken();
        Integer channelId = 1;
        RestMockHttpRequest request = post("/api/channels/"  + channelId + "/members")
                .setContentType("application/json")
                .setBody(Map.of("accountId", 10000));
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 参加していないチャンネルでアカウントを招待できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;
        RestMockHttpRequest request = post("/api/channels/"  + channelId + "/members")
                .setContentType("application/json")
                .setBody(Map.of("accountId", 10000));
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 存在しないチャンネルでアカウントを招待できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        RestMockHttpRequest request = post("/api/channels/"  + channelId + "/members")
                .setContentType("application/json")
                .setBody(Map.of("accountId", 10000));
        HttpResponse response = sendRequest(request);
        assertEquals(404, response.getStatusCode());
    }
}
