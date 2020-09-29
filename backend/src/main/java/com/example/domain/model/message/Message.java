package com.example.domain.model.message;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;

public class Message {

    private final MessageId messageId;

    private final ChannelId channelId;

    private final AccountId accountId;

    private final MessageText text;

    private final MessageType type;

    private final SendDateTime sendDateTime;

    public Message(MessageId messageId, ChannelId channelId, AccountId accountId, MessageText text,
            MessageType type, SendDateTime sendDateTime) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.accountId = accountId;
        this.text = text;
        this.type = type;
        this.sendDateTime = sendDateTime;
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

    public MessageText text() {
        return text;
    }

    public MessageType type() {
        return type;
    }

    public SendDateTime sendDateTime() {
        return sendDateTime;
    }
}
