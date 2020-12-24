package com.example.presentation.restapi.message;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageHistoryArchiveActionIT extends ExampleChatRestTestBase {

    @Test
    public void メッセージ履歴ファイルのエクスポートとダウンロードができる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/history/archive");
        HttpResponse response = sendRequest(request);

        assertEquals(200, response.getStatusCode());
        validateByOpenAPI("post-channels-channelId-history-archive", request, response);

        ExportingResponse exportingResponse = new ObjectMapper().readValue(response.getBodyString(), ExportingResponse.class);
        RestMockHttpRequest downLoadRequest = get("/api/channels/" + channelId + "/history/archive/" + exportingResponse.fileKey);
        HttpResponse downloadResponse = sendRequest(downLoadRequest);

        assertEquals(200, downloadResponse.getStatusCode());
        validateByOpenAPI("get-channels-channelId-history-archive", downLoadRequest, downloadResponse);
    }

    @Test
    public void ログインしていない場合はメッセージ履歴ファイルのエクスポートはできない() {
        loadCsrfToken();
        Integer channelId = 1;

        RestMockHttpRequest request = post("/api/channels/" + channelId + "/history/archive");
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("post-channels-channelId-history-archive", request, response);
    }

    @Test
    public void 参加していないチャンネルではメッセージ履歴ファイルのエクスポートはできない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;

        RestMockHttpRequest request = post("/api/channels/" + channelId + "/history/archive");
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("post-channels-channelId-history-archive", request, response);
    }

    @Test
    public void 存在しないチャンネルではメッセージ履歴ファイルのエクスポートはできない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/history/archive");
        HttpResponse response = sendRequest(request);

        assertEquals(404, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.notfound");
        validateByOpenAPI("post-channels-channelId-history-archive", request, response);
    }

    @Test
    public void ログインしていない場合はメッセージ履歴ファイルをダウンロードができない() {
        Integer channelId = 1;
        String dummyFileKey = "dummy";

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/history/archive/" + dummyFileKey);
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-channels-channelId-history-archive", request, response);
    }

    @Test
    public void 参加していないチャンネルではメッセージ履歴ファイルをダウンロードができない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;
        String dummyFileKey = "dummy";

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/history/archive/" + dummyFileKey);
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-channels-channelId-history-archive", request, response);
    }

    @Test
    public void 存在しないチャンネルではメッセージ履歴ファイルをダウンロードができない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;
        String dummyFileKey = "dummy";

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/history/archive/" + dummyFileKey);
        HttpResponse response = sendRequest(request);

        assertEquals(404, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.notfound");
        validateByOpenAPI("get-channels-channelId-history-archive", request, response);
    }

    public static class ExportingResponse {
        public String fileKey;
    }
}
