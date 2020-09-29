package com.example.domain.model.channel;

import java.util.Collections;
import java.util.List;

public class ChannelMembers {

    private final ChannelOwner owner;

    private final List<ChannelMember> members;

    public ChannelMembers(ChannelOwner owner, List<ChannelMember> members) {
        this.owner = owner;
        this.members = members;
    }

    public ChannelOwner owner() {
        return this.owner;
    }

    public List<ChannelMember> members() {
        return Collections.unmodifiableList(members);
    }

}
