package com.example.infrastructure.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "channel_owner")
@Access(AccessType.FIELD)
public class ChannelOwnerEntity {

    @Id
    public Long channelId;

    private Long accountId;

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
}
