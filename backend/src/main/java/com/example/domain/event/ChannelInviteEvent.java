package com.example.domain.event;

import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelName;
import com.example.domain.model.channel.ChatBot;

public class ChannelInviteEvent implements Event {

    private final ChannelId channelId;

    private final ChannelName channelName;

    private final ChatBot chatBot;

    public ChannelInviteEvent(ChannelId channelId, ChannelName channelName, ChatBot chatBot) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.chatBot = chatBot;
    }

    public ChannelId channelId() {
        return channelId;
    }

    public ChannelName channelName() {
        return channelName;
    }

    public ChatBot chatBot() {
        return chatBot;
    }

}
