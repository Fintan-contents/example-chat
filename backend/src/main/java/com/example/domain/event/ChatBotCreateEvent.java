package com.example.domain.event;

import com.example.domain.model.channel.ChatBot;

public class ChatBotCreateEvent implements Event {

    private final ChatBot chatBot;

    public ChatBotCreateEvent(ChatBot chatBot) {
        this.chatBot = chatBot;
    }

    public ChatBot chatBot() {
        return chatBot;
    }
}
