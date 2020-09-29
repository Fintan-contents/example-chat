package com.example.infrastructure.event;

import com.example.application.service.message.MessageNotifier;
import com.example.domain.model.message.Message;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class RedisPublishingNotifier implements MessageNotifier {

    private final RedisPublisher redisPublisher;

    public RedisPublishingNotifier(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

    @Override
    public void notify(Message message) {
        redisPublisher.sendMessage(message.channelId(), message);
    }
}
