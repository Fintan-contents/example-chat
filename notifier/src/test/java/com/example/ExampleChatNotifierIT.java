package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.presentation.websocket.WebSocketEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import nablarch.core.repository.SystemRepository;

public class ExampleChatNotifierIT {

    private static final long TIMEOUT_VALUE = 100;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private static Server server;
    private static StatefulRedisPubSubConnection<String, String> connection;
    private static RedisPubSubCommands<String, String> commands;

    @BeforeAll
    static void init() throws Exception {
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource("src/main/webapp"));
        server = new Server(9081);
        server.setHandler(context);

        ServerContainer serverContainer = WebSocketServerContainerInitializer.configureContext(context);
        serverContainer.addEndpoint(WebSocketEndpoint.class);

        server.start();

        RedisClient redisClient = SystemRepository.get("redisClient");
        connection = redisClient.connectPubSub();
        commands = connection.sync();
    }

    @AfterAll
    static void destroy() throws Exception {
        if (connection != null) {
            connection.close();
        }
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void 通知ができる() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            ObjectMapper om = new ObjectMapper();
            commands.publish("notification", om.writeValueAsString(
                    Map.of("destinations", List.of("account1", "account2"),
                            "payload", Map.of("content", "foo"))));
            commands.publish("notification", om.writeValueAsString(
                    Map.of("destinations", List.of("account1", "account3"),
                            "payload", Map.of("content", "bar"))));
            //destinationsにaccount1がいない
            commands.publish("notification", om.writeValueAsString(
                    Map.of("destinations", List.of("account2", "account3"),
                            "payload", Map.of("content", "baz"))));
            //不明なチャンネル
            commands.publish("unknownchannel", om.writeValueAsString(
                    Map.of("destinations", List.of("account1", "account2"),
                            "payload", Map.of("content", "qux"))));

            assertEquals("foo", om.readTree(client.dequeueMessage()).get("content").asText());
            assertEquals("bar", om.readTree(client.dequeueMessage()).get("content").asText());
            assertNull(client.dequeueMessage());
        }
    }

    @Test
    void フォーマットが異常なメッセージは通知されない() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            ObjectMapper om = new ObjectMapper();
            commands.publish("notification", om.writeValueAsString(
                    Map.of("unknownProperty", List.of("account1", "account2"))));

            assertNull(client.dequeueMessage());
        }
    }

    @Test
    void destinationsが存在しないメッセージは通知されない() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            ObjectMapper om = new ObjectMapper();
            commands.publish("notification", om.writeValueAsString(
                    Map.of("payload", Map.of("content", "foo"))));

            assertNull(client.dequeueMessage());
        }
    }

    @Test
    void destinationsが配列でないメッセージは通知されない() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            ObjectMapper om = new ObjectMapper();
            commands.publish("notification", om.writeValueAsString(
                    Map.of("destinations", "account1",
                            "payload", Map.of("content", "foo"))));

            assertNull(client.dequeueMessage());
        }
    }

    @Test
    void destinationsの要素にスカラー値でないものを含むメッセージは通知されない() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            ObjectMapper om = new ObjectMapper();
            commands.publish("notification", om.writeValueAsString(
                    Map.of("destinations", List.of("account1", List.of("account2")),
                            "payload", Map.of("content", "foo"))));

            assertNull(client.dequeueMessage());
        }
    }

    @Test
    void payloadが存在しないメッセージは通知されない() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            ObjectMapper om = new ObjectMapper();
            commands.publish("notification", om.writeValueAsString(
                    Map.of("destinations", List.of("account1", "account2"))));

            assertNull(client.dequeueMessage());
        }
    }

    @Test
    void payloadがnullのメッセージは通知されない() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> message = new HashMap<>();
            message.put("destinations", List.of("account1", "account2"));
            message.put("payload", null);
            commands.publish("notification", om.writeValueAsString(message));

            assertNull(client.dequeueMessage());
        }
    }

    @Test
    void JSONでないメッセージは通知されない() throws Exception {
        String token = UUID.randomUUID().toString();
        commands.set(token, "account1");

        WebSocketClient client = new WebSocketClient();
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(client,
                URI.create("ws://localhost:9081/notification?token=" + token))) {
            commands.publish("notification", "unknown format message");

            assertNull(client.dequeueMessage());
        }
    }

    @ClientEndpoint
    public static class WebSocketClient {

        private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        @OnMessage
        public void onMessage(String message) {
            queue.add(message);
        }

        public String dequeueMessage() throws InterruptedException {
            return queue.poll(TIMEOUT_VALUE, TIMEOUT_UNIT);
        }
    }
}
