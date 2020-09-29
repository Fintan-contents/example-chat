package com.example.domain.repository;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.LastReadMessage;

public interface ReadMessageRepository {

    LastReadMessage findBy(ChannelId channelId, AccountId accountId);

    void add(LastReadMessage lastReadMessage);

    void update(LastReadMessage lastReadMessage);

    void remove(ChannelId channelId, AccountId accountId);

}
