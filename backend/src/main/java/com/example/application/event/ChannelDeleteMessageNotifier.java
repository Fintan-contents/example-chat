package com.example.application.event;

import com.example.domain.event.ChannelDeleteEvent;
import com.example.domain.model.channel.ChatBot;
import com.example.domain.model.message.MessageText;
import com.example.application.service.message.MessagePostingService;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelDeleteMessageNotifier implements EventListener<ChannelDeleteEvent> {

    private final MessagePostingService messagePostingService;

    public ChannelDeleteMessageNotifier(MessagePostingService messagePostingService) {
        this.messagePostingService = messagePostingService;
    }

    @Override
    public void handle(ChannelDeleteEvent event) {
        String message = String.format("%sさんが #%s チャンネルを削除しました", event.deleteUserName().value(),
                event.deleteChannelName().value());
        MessageText messageText = new MessageText(message);
        event.chatBots()
                .forEach(bot -> messagePostingService.postText(bot.channelId(), ChatBot.SystemUser.ID, messageText));
    }
}
