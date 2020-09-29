package com.example.application.service.channel;

import com.example.domain.repository.ChannelMemberRepository;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelMembers;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelMemberSearchService {

    private final ChannelMemberRepository channelMemberRepository;

    public ChannelMemberSearchService(ChannelMemberRepository channelMemberRepository) {
        this.channelMemberRepository = channelMemberRepository;
    }

    public ChannelMembers findBy(ChannelId channelId) {
        return channelMemberRepository.findBy(channelId);
    }
}
