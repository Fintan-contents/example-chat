package com.example.application.event;

import com.example.domain.event.ChatBotCreateEvent;
import com.example.application.service.message.MessagePostingService;
import com.example.domain.model.channel.ChatBot;
import com.example.domain.model.message.MessageText;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChatBotCreateMessageNotifier implements EventListener<ChatBotCreateEvent> {

    private final MessagePostingService messagePostingService;

    public ChatBotCreateMessageNotifier(MessagePostingService messagePostingService) {
        this.messagePostingService = messagePostingService;
    }

    @Override
    public void handle(ChatBotCreateEvent event) {
        String message = "ようこそ。私は chatbot です！お役に立てるよう頑張ります。";
        MessageText messageText = new MessageText(message);
        messagePostingService.postText(event.chatBot().channelId(), ChatBot.SystemUser.ID, messageText);
    }
}
