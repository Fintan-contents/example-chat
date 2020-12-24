package com.example.domain.repository;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.Message;
import com.example.domain.model.message.MessageId;
import com.example.domain.model.message.Messages;

public interface MessageRepository {

    void add(Message message);

    void remove(ChannelId channelId, AccountId accountId);

    Messages findBy(ChannelId channelId, MessageId nextMessageId);
}
