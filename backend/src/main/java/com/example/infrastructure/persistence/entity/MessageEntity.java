package com.example.infrastructure.persistence.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "message")
@Access(AccessType.FIELD)
public class MessageEntity {

    @Id
    private Long messageId;

    private Long channelId;

    private Long accountId;

    private String text;

    private String type;

    private Timestamp sendDateTime;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getSendDateTime() {
        return sendDateTime;
    }

    public void setSendDateTime(Timestamp sendDateTime) {
        this.sendDateTime = sendDateTime;
    }
}
