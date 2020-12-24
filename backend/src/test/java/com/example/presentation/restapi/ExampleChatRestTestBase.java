package com.example.presentation.restapi;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.After;
import org.junit.Before;
import org.openapi4j.core.validation.ValidationException;

import com.example.openapi.OpenApiValidator;
import com.example.system.nablarch.FlywayExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import nablarch.test.core.http.SimpleRestTestSupport;

public abstract class ExampleChatRestTestBase extends SimpleRestTestSupport {

    private final OpenApiValidator openApiValidator = new OpenApiValidator(Path.of("../api-document/openapi.yaml"));

    private final ObjectMapper om = new ObjectMapper();

    private StatefulRedisPubSubConnection<String, String> connection;

    @Before
    public void refreshDB() {
        FlywayExecutor flywayExecutor = SystemRepository.get("dbMigration");
        flywayExecutor.migrate(true);
    }

    public void loadCsrfToken() {
        RestMockHttpRequest request = get("/api/csrf_token");
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());
    }

    public void login(String mailAddress, String password) throws Exception {
        loadCsrfToken();

        RestMockHttpRequest request = post("/api/login")
                .setContentType("application/json")
                .setBody(Map.of("mailAddress", mailAddress, "password", password));
        HttpResponse response = sendRequest(request);
        assertEquals(200, response.getStatusCode());
        JsonNode root = om.readTree(response.getBodyString());
        assertEquals("COMPLETE", root.get("status").asText());

        loadCsrfToken();
    }

    public void logout() {
        RestMockHttpRequest request = post("/api/logout");
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        loadCsrfToken();
    }

    public void validateByOpenAPI(String operationId, RestMockHttpRequest request, HttpResponse response) {
        try {
            openApiValidator.validate(operationId, request, response);
        } catch (ValidationException e) {
            fail(e.toString());
        }
    }

    public void validateByOpenAPI(String operationId, HttpResponse response) {
        try {
            openApiValidator.validateResponse(operationId, response);
        } catch (ValidationException e) {
            fail(e.toString());
        }
    }

    public BlockingDeque<Map<String, Object>> connectNotificationQueue() {
        BlockingDeque<Map<String, Object>> receivedMessageQueue = new LinkedBlockingDeque<>();
        RedisClient redisClient = RedisClient.create(SystemRepository.getString("nablarch.lettuce.simple.uri"));
        connection = redisClient.connectPubSub();
        connection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                Map<String, Object> json;
                try {
                    json = om.readValue(message, Map.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                receivedMessageQueue.add((Map<String, Object>) json.get("payload"));
            }
        });
        RedisPubSubCommands<String, String> commands = connection.sync();
        commands.subscribe("notification");
        return receivedMessageQueue;
    }

    @After
    public void closeConnection() {
        if (connection != null) {
            connection.close();
        }
    }
}
