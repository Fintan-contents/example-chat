package com.example.system.nablarch.redis;

import java.util.ArrayList;
import java.util.List;

import com.example.domain.model.AccountId;
import com.example.domain.model.Destinations;
import com.example.domain.model.Payload;
import com.example.system.nablarch.handler.Message;
import com.example.system.nablarch.handler.MessageConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class RedisSubscriber extends RedisPubSubAdapter<String, String> {

    private static final Logger LOGGER = LoggerManager.get(RedisSubscriber.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageConsumer messageConsumer;

    public RedisSubscriber(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void message(String channel, String message) {

        JsonNode root;
        try {
            root = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn("Invalid JSON", e);
            }
            return;
        }

        JsonNode destinationsNode = root.get("destinations");
        if (destinationsNode == null || destinationsNode.isArray() == false) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn("Invalid JSON: " + message);
            }
            return;
        }
        List<AccountId> destinations = new ArrayList<>(destinationsNode.size());
        for (int i = 0; i < destinationsNode.size(); i++) {
            JsonNode destinationNode = destinationsNode.get(i);
            if (destinationNode.isValueNode() == false) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.logWarn("Destication(" + i + ") is not value node: " + destinationNode);
                }
                return;
            }
            destinations.add(new AccountId(destinationNode.asText()));
        }

        JsonNode payloadNode = root.get("payload");
        if (payloadNode == null || payloadNode.isNull()) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn("Payload is null");
            }
            return;
        }
        String payload = payloadNode.toString();

        Message data = new Message();
        data.setRequestPath(channel);
        data.setDestinations(new Destinations(destinations));
        data.setPayload(new Payload(payload));

        messageConsumer.consume(data);
    }
}
