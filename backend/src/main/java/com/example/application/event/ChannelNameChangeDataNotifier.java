package com.example.application.event;

import com.example.domain.event.ChannelNameChangeEvent;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelName;
import com.example.infrastructure.event.RedisPublisher;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelNameChangeDataNotifier implements EventListener<ChannelNameChangeEvent> {

    private final RedisPublisher redisPublisher;

    public ChannelNameChangeDataNotifier(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

    @Override
    public void handle(ChannelNameChangeEvent event) {
        ChannelName oldChannelName = event.oldChannel().name();
        ChannelName newChannelName = event.newChannel().name();

        if (oldChannelName.value().equals(newChannelName.value())) {
            return;
        }

        ChannelId channelId = event.newChannel().id();
        redisPublisher.notifyChannelNameChange(channelId, oldChannelName, newChannelName);
    }
}
