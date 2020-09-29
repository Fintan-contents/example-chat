package com.example.domain.model.channel;

import com.example.domain.model.account.AccountId;

import java.util.List;

public class Channel {

    private final ChannelId id;

    private final ChannelName name;

    private final ChannelOwner owner;

    private final List<ChannelMember> members;

    private final ChannelType type;

    public Channel(ChannelId id, ChannelName name, ChannelOwner owner, List<ChannelMember> members, ChannelType type) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.members = members;
        this.type = type;
    }

    public ChannelId id() {
        return id;
    }

    public ChannelName name() {
        return name;
    }

    public ChannelOwner owner() {
        return owner;
    }

    public List<ChannelMember> members() {
        return members;
    }

    public ChannelType type() {
        return type;
    }

    public boolean hasMember(AccountId accountId) {
        return members.stream().anyMatch(member -> member.accountId().equals(accountId));
    }

    public Channel changeName(ChannelName newChannelName) {
        return new Channel(id, newChannelName, owner, members, type);
    }

}
