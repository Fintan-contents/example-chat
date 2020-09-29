package com.example.infrastructure.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "read_message")
@Access(AccessType.FIELD)
public class ReadMessageEntity {

    @Id
    private Long channelId;

    @Id
    private Long accountId;

    private Long lastReadMessageId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
