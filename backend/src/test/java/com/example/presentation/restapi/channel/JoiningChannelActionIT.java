package com.example.presentation.restapi.channel;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoiningChannelActionIT extends ExampleChatRestTestBase {

    @Test
    public void チャンネルを離脱できる() throws Exception {
        login("user2@example.com", "pass123-");
        Integer channelId = 1;
        RestMockHttpRequest request = delete("/api/channels/" + channelId + "/members/me");
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        validateByOpenAPI("delete-channels-channelId-members-me", request, response);

        // チャンネルオーナーに影響が無いこと
        logout();
        login("user1@example.com", "pass123-");
        request = get("/api/channels/" + channelId);
        response = sendRequest(request);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void ログインしていない場合はチャンネルを離脱できない() {
        loadCsrfToken();
        Integer channelId = 1;
        RestMockHttpRequest request = delete("/api/channels/" + channelId + "/members/me");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 自分がオーナーのチャンネルは離脱できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        RestMockHttpRequest request = delete("/api/channels/" + channelId + "/members/me");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void ChatBotのチャンネルは離脱できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 2;
        RestMockHttpRequest request = delete("/api/channels/" + channelId + "/members/me");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 参加していないチャンネルを離脱できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;
        RestMockHttpRequest request = delete("/api/channels/" + channelId + "/members/me");
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 存在しないチャンネルを離脱できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        RestMockHttpRequest request = delete("/api/channels/" + channelId + "/members/me");
        HttpResponse response = sendRequest(request);
        assertEquals(404, response.getStatusCode());
    }
}
