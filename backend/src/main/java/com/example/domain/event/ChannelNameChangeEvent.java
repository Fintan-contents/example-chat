package com.example.domain.event;

import com.example.domain.model.channel.Channel;

public class ChannelNameChangeEvent implements Event {

    private final Channel oldChannel;

    private final Channel newChannel;

    public ChannelNameChangeEvent(Channel oldChannel, Channel newChannel) {
        this.oldChannel = oldChannel;
        this.newChannel = newChannel;
    }

    public Channel oldChannel() {
        return oldChannel;
    }

    public Channel newChannel() {
        return newChannel;
    }
}
