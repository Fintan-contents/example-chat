package com.example.domain.model.channel;

import java.util.Collections;
import java.util.List;

public class Channels {

    private final List<Channel> channels;

    public Channels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> asList() {
        return Collections.unmodifiableList(channels);
    }

    public boolean isEmpty() {
        return channels.isEmpty();
    }
}
