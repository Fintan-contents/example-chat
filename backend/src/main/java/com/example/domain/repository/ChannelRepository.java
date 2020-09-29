package com.example.domain.repository;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.Channel;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelName;
import com.example.domain.model.channel.Channels;

public interface ChannelRepository {

    void add(Channel channel);

    void update(Channel channel);

    void remove(Channel channel);

    void removeAll(Channels channels);

    Channel findById(ChannelId channelId);

    Channels findMembersChannel(AccountId accountId);

    Channels findOwnersChannel(AccountId accountId);

    boolean existsById(ChannelId channelId);

    boolean existsBy(ChannelName channelName);

}
