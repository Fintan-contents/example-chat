package com.example.presentation.restapi.channel;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ChannelsActionIT extends ExampleChatRestTestBase {

    @Test
    public void チャンネル一覧を取得できる() throws Exception {
        login("user1@example.com", "pass123-");
        RestMockHttpRequest request = get("/api/channels");
        HttpResponse response = sendRequest(request);

        assertEquals(200, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertThat("$", Matchers.hasSize(2));

        validateByOpenAPI("get-channels", request, response);
    }

    @Test
    public void ログインしていない場合はチャンネル一覧を取得できない() {
        RestMockHttpRequest request = get("/api/channels");
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-channels", request, response);
    }

    @Test
    public void チャンネルを作成できる() throws Exception {
        login("user1@example.com", "pass123-");
        String channelName = UUID.randomUUID().toString();
        RestMockHttpRequest request = post("/api/channels")
                .setContentType("application/json")
                .setBody(Map.of("channelName", channelName));
        HttpResponse response = sendRequest(request);

        assertEquals(204, response.getStatusCode());
        validateByOpenAPI("post-channels", request, response);
    }

    @Test
    public void 既に存在する名前のチャンネルは作成できない() throws Exception {
        login("user1@example.com", "pass123-");
        String channelName = "general";
        RestMockHttpRequest request = post("/api/channels")
                .setContentType("application/json")
                .setBody(Map.of("channelName", channelName));
        HttpResponse response = sendRequest(request);

        assertEquals(409, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.conflict");
        validateByOpenAPI("post-channels", request, response);
    }

    @Test
    public void チャンネル作成のチャンネル名が不正() throws Exception {
        login("user1@example.com", "pass123-");
        String[] invalidChannelNames = new String[] { "", " " };

        Arrays.stream(invalidChannelNames).forEach(invalidChannelName -> {
            RestMockHttpRequest request = post("/api/channels")
                    .setContentType("application/json")
                    .setBody(Map.of("channelName", invalidChannelName));
            HttpResponse response = sendRequest(request);

            assertEquals(400, response.getStatusCode());
            JsonAssert.with(response.getBodyString())
                    .assertEquals("$.code", "request");
            validateByOpenAPI("post-channels", request, response);
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = post("/api/channels").setContentType("application/json");
        HttpResponse response = sendRequest(request);

        assertEquals(400, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "request");
        validateByOpenAPI("post-channels", response);
    }

    @Test
    public void ログインしていない場合はチャンネルを作成できない() {
        loadCsrfToken();
        String channelName = UUID.randomUUID().toString();

        RestMockHttpRequest request = post("/api/channels")
                .setContentType("application/json")
                .setBody(Map.of("channelName", channelName));
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("post-channels", request, response);
    }
}
