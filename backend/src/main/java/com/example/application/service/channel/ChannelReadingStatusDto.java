package com.example.application.service.channel;

public class ChannelReadingStatusDto {

    private Long channelId;

    private String channelName;

    private String type;

    private Long latestMessageId;

    private Long lastReadMessageId;

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

    public Long getLatestMessageId() {
        return latestMessageId;
    }

    public void setLatestMessageId(Long latestMessageId) {
        this.latestMessageId = latestMessageId;
    }

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
