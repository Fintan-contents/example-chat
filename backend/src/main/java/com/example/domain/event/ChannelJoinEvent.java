package com.example.domain.event;

import com.example.domain.model.channel.Channel;

public class ChannelJoinEvent implements Event {

    private final Channel channel;

    public ChannelJoinEvent(Channel channel) {
        this.channel = channel;
    }

    public Channel channel() {
        return channel;
    }
}
