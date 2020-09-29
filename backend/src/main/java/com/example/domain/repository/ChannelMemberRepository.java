package com.example.domain.repository;

import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelMember;
import com.example.domain.model.channel.ChannelMembers;

public interface ChannelMemberRepository {

    ChannelMembers findBy(ChannelId channelId);

    void add(ChannelId channelId, ChannelMember channelMember);

    void remove(ChannelId channelId, ChannelMember channelMember);

}
