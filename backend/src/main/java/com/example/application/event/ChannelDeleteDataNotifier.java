package com.example.application.event;

import com.example.domain.event.ChannelDeleteEvent;
import com.example.infrastructure.event.RedisPublisher;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelDeleteDataNotifier implements EventListener<ChannelDeleteEvent> {

    private final RedisPublisher redisPublisher;

    public ChannelDeleteDataNotifier(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

    @Override
    public void handle(ChannelDeleteEvent event) {
        redisPublisher.notifyChannelDelete(event.deleteChannelId(), event.channelMembers());
    }
}
