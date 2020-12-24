package com.example.application.service.channel;

import com.example.domain.repository.ChannelRepository;
import com.example.domain.model.channel.Channel;
import com.example.domain.model.channel.ChannelId;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelSearchService {

    private final ChannelRepository channelRepository;

    public ChannelSearchService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel findBy(ChannelId channelId) {
        return channelRepository.findById(channelId);
    }
}
