package com.example.presentation.restapi.message;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MessageReadActionIT extends ExampleChatRestTestBase {

    @Test
    public void メッセージを既読にできる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        Integer messageId = 1;
        RestMockHttpRequest request = put("/api/read/" + channelId)
                .setContentType("application/json")
                .setBody(Map.of("messageId", messageId));
        HttpResponse response = sendRequest(request);

        assertEquals(204, response.getStatusCode());
        validateByOpenAPI("put-read-channelId", request, response);
    }

    @Test
    public void メッセージを既読にするメッセージIDが不正() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        String[] invalidMessageIds = new String[] { "", " " };

        Arrays.stream(invalidMessageIds).forEach(invalidMessageId -> {
            RestMockHttpRequest request = put("/api/read/" + channelId)
                    .setContentType("application/json")
                    .setBody(Map.of("messageId", invalidMessageId));
            HttpResponse response = sendRequest(request);

            assertEquals(400, response.getStatusCode());
            JsonAssert.with(response.getBodyString())
                    .assertEquals("$.code", "request");
            validateByOpenAPI("put-read-channelId", response);
        });

        // 項目として送信しないケース
        RestMockHttpRequest request = put("/api/read/" + channelId)
                .setContentType("application/json");
        HttpResponse response = sendRequest(request);

        assertEquals(400, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "request");
        validateByOpenAPI("put-read-channelId", response);
    }

    @Test
    public void ログインしていない場合はメッセージを既読にできない() {
        loadCsrfToken();
        Integer channelId = 1;
        Integer messageId = 1;

        RestMockHttpRequest request = put("/api/read/" + channelId)
                .setContentType("application/json")
                .setBody(Map.of("messageId", messageId));
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("put-read-channelId", response);

    }

    @Test
    public void 参加していないチャンネルにはメッセージを既読にできない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;
        Integer messageId = 1;

        RestMockHttpRequest request = put("/api/read/" + channelId)
                .setContentType("application/json")
                .setBody(Map.of("messageId", messageId));
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("put-read-channelId", response);
    }

    @Test
    public void 存在しないチャンネルにはメッセージを既読にできない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        Integer messageId = 1;
        RestMockHttpRequest request = put("/api/read/" + channelId)
                .setContentType("application/json")
                .setBody(Map.of("messageId", messageId));
        HttpResponse response = sendRequest(request);
        assertEquals(404, response.getStatusCode());
    }
}
