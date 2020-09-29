package com.example.presentation.restapi.system;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;

import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;

public class SystemInfoMessageActionIT extends ExampleChatRestTestBase {

    @Test
    public void システムが管理するメッセージの一覧を取得できる() throws Exception {
        RestMockHttpRequest request = get("/api/systeminfo/messages");
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());

        Map<String, String> result = new ObjectMapper().readValue(response.getBodyString(), Map.class);

        // 除外ルールが適用されていることを確認
        for (String key : result.keySet()) {
            assertFalse(key.startsWith("nablarch.core.validation"));
            assertFalse(key.startsWith("nablarch.common.code.validator"));
        }

        // フォーマット前のメッセージを取得できていることを確認
        assertEquals("{0}を入力してください。", result.get("errors.required"));

        validateByOpenAPI("get-systeminfo-messages", request, response);
    }
}
