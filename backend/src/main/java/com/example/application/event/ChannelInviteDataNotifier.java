package com.example.application.event;

import com.example.domain.event.ChannelInviteEvent;
import com.example.infrastructure.event.RedisPublisher;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelInviteDataNotifier implements EventListener<ChannelInviteEvent> {

    private final RedisPublisher redisPublisher;

    public ChannelInviteDataNotifier(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

    @Override
    public void handle(ChannelInviteEvent event) {
        redisPublisher.notifyChannelInvite(event.channelId(), event.channelName());
    }
}
