package com.example.domain.model.message;

import java.util.Collections;
import java.util.List;

public class Messages {

    private final List<Message> messages;

    private final MessageId nextMessageId;

    public Messages(List<Message> messages, MessageId nextMessageId) {
        this.messages = messages;
        this.nextMessageId = nextMessageId;
    }

    public List<Message> messages() {
        return Collections.unmodifiableList(messages);
    }

    public MessageId nextMessageId() {
        return nextMessageId;
    }
}
