package com.example.presentation.restapi.channel;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.jayway.jsonassert.JsonAssert;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChannelInvitableAccountsActionIT extends ExampleChatRestTestBase {

    @Test
    public void 招待可能なアカウント一覧を取得できる() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 1;

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/invitable_accounts");
        HttpResponse response = sendRequest(request);

        assertEquals(200, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertThat("$", Matchers.hasSize(4))
                .assertEquals("$[0].id", 3)
                .assertEquals("$[0].name", "2FAUser")
                .assertEquals("$[1].id", 10000)
                .assertEquals("$[1].name", "openapi-example")
                .assertEquals("$[2].id", 10010)
                .assertEquals("$[2].name", "openapi-example2")
                .assertEquals("$[3].id", 10020)
                .assertEquals("$[3].name", "openapi-example3");
        validateByOpenAPI("get-channels-channelId-invitable_members", request, response);
    }

    @Test
    public void ログインしていない場合は招待可能なアカウントを取得できない() {
        Integer channelId = 1;

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/invitable_accounts");
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-channels-channelId-invitable_members", request, response);
    }

    @Test
    public void 参加していないチャンネルの招待可能なアカウントは取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = 20001;

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/invitable_accounts");
        HttpResponse response = sendRequest(request);

        assertEquals(403, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "access.denied");
        validateByOpenAPI("get-channels-channelId-invitable_members", request, response);
    }

    @Test
    public void 存在しないチャンネルの招待可能なアカウントは取得できない() throws Exception {
        login("user1@example.com", "pass123-");
        Integer channelId = Integer.MAX_VALUE;

        RestMockHttpRequest request = get("/api/channels/" + channelId + "/invitable_accounts");
        HttpResponse response = sendRequest(request);

        assertEquals(404, response.getStatusCode());
        JsonAssert.with(response.getBodyString())
                .assertEquals("$.code", "channel.notfound");
        validateByOpenAPI("get-channels-channelId-invitable_members", request, response);
    }
}
