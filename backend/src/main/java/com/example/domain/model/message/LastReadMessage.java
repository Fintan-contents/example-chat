package com.example.domain.model.message;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.MessageId;

public class LastReadMessage {

    private final MessageId messageId;

    private final ChannelId channelId;

    private final AccountId accountId;

    public LastReadMessage(MessageId messageId, ChannelId channelId, AccountId accountId) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.accountId = accountId;
    }

    public static LastReadMessage empty(ChannelId channelId, AccountId accountId) {
        return new LastReadMessage(new MessageId(0L), channelId, accountId);
    }

    public MessageId messageId() {
        return messageId;
    }

    public ChannelId channelId() {
        return channelId;
    }

    public AccountId accountId() {
        return accountId;
    }

    public boolean isLastMessageId(MessageId messageId) {
        return this.messageId.equals(messageId);
    }
}