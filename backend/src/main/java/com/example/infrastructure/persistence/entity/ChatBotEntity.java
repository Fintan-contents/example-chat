package com.example.infrastructure.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "chat_bot")
@Access(AccessType.FIELD)
public class ChatBotEntity {

    @Id
    private Long accountId;

    private Long channelId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

}
