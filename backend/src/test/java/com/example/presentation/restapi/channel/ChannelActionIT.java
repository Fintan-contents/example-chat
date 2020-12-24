package com.example.presentation.restapi.channel;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChannelActionIT extends ExampleChatRestTestBase {

    @Test
    public void チャンネルを取得できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        String channelName = "general";

        RestMockHttpRequest request = get("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(200, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.name", channelName)
                .assertEquals("$.id", channelId);
        validateByOpenAPI("get-channels-channelId", request, response);
    }

    @Test
    public void ログインしていない場合はチャンネルを取得できない() {
        Integer channelId = 1;

        RestMockHttpRequest request = get("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-channels-channelId", request, response);
    }

    @Test
    public void 参加していないチャンネルは取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;

        RestMockHttpRequest request = get("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-channels-channelId", request, response);
    }

    @Test
    public void 存在しないチャンネルは取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;

        RestMockHttpRequest request = get("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(404, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.notfound");
        validateByOpenAPI("get-channels-channelId", request, response);
    }

    @Test
    public void チャンネルを削除できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;

        RestMockHttpRequest request = delete("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(204, response.getStatusCode());
        validateByOpenAPI("delete-channels-channelId", request, response);

        RestMockHttpRequest channelRequest = get("/api/channels/" + channelId);
        HttpResponse channelResponse = sendRequest(channelRequest);

        assertEquals(404, channelResponse.getStatusCode());
    }

    @Test
    public void ログインしていない場合はチャンネルを削除できない() {
        loadCsrfToken();
        Integer channelId = 1;

        RestMockHttpRequest request = delete("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("delete-channels-channelId", request, response);
    }

    @Test
    public void 自分以外がオーナーのチャンネルは削除できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;

        RestMockHttpRequest request = delete("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("delete-channels-channelId", request, response);
    }

    @Test
    public void 存在しないチャンネルは削除できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;

        RestMockHttpRequest request = delete("/api/channels/" + channelId);
        HttpResponse response = sendRequest(request);

        assertEquals(404, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.notfound");
        validateByOpenAPI("delete-channels-channelId", request, response);
    }
}
