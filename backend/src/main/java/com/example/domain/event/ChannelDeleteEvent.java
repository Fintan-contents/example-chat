package com.example.domain.event;

import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelMembers;
import com.example.domain.model.channel.ChannelName;
import com.example.domain.model.channel.ChatBot;
import com.example.domain.model.account.UserName;

import java.util.List;

public class ChannelDeleteEvent implements Event {

    private final UserName deleteUserName;

    private final ChannelId deleteChannelId;

    private final ChannelName deleteChannelName;

    private final ChannelMembers channelMembers;

    private final List<ChatBot> chatBots;

    public ChannelDeleteEvent(UserName deleteUserName, ChannelId deleteChannelId, ChannelName deleteChannelName, ChannelMembers channelMembers,
            List<ChatBot> chatBots) {
        this.deleteUserName = deleteUserName;
        this.deleteChannelId = deleteChannelId;
        this.deleteChannelName = deleteChannelName;
        this.channelMembers = channelMembers;
        this.chatBots = chatBots;
    }

    public UserName deleteUserName() {
        return deleteUserName;
    }

    public ChannelId deleteChannelId() {
        return deleteChannelId;
    }

    public ChannelName deleteChannelName() {
        return deleteChannelName;
    }

    public ChannelMembers channelMembers() {
        return channelMembers;
    }

    public List<ChatBot> chatBots() {
        return chatBots;
    }
}
