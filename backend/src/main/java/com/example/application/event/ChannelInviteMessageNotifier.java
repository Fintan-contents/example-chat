package com.example.application.event;

import com.example.domain.event.ChannelInviteEvent;
import com.example.domain.model.channel.ChatBot;
import com.example.domain.model.message.MessageText;
import com.example.application.service.message.MessagePostingService;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelInviteMessageNotifier implements EventListener<ChannelInviteEvent> {

    private final MessagePostingService messagePostingService;

    public ChannelInviteMessageNotifier(MessagePostingService messagePostingService) {
        this.messagePostingService = messagePostingService;
    }

    @Override
    public void handle(ChannelInviteEvent event) {
        String message = String.format("#%s チャンネルに招待されました。", event.channelName().value());
        MessageText messageText = new MessageText(message);
        messagePostingService.postText(event.chatBot().channelId(), ChatBot.SystemUser.ID, messageText);
    }
}
