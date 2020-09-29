package com.example.infrastructure.persistence.entity;

import javax.persistence.*;

@Entity
@Table(name = "channel")
@Access(AccessType.FIELD)
public class ChannelEntity {

    @Id
    public Long channelId;

    private String channelName;

    private String type;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
